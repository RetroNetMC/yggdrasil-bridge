package hu.retronet.mc.authserver.controller;

import hu.retronet.mc.authserver.model.Property;
import hu.retronet.mc.authserver.model.UserProfile;
import hu.retronet.mc.authserver.model.UserProperties;
import hu.retronet.mc.authserver.model.request.AuthenticationRequest;
import hu.retronet.mc.authserver.model.response.AuthenticationResponse;
import hu.retronet.mc.common.BaseAuthServerController;
import hu.retronet.mc.common.conditions.ConditionalOnServerType;
import hu.retronet.mc.common.entity.AuthSession;
import hu.retronet.mc.common.entity.User;
import hu.retronet.mc.common.exceptions.InvalidCredentialsException;
import hu.retronet.mc.common.model.IErrorResponse;
import hu.retronet.mc.common.model.ServerType;
import hu.retronet.mc.common.model.YggdrasilError;
import hu.retronet.mc.common.repository.AuthSessionRepository;
import hu.retronet.mc.common.service.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Tag(name = "Authentication")
@RestController
@RequestMapping("/authenticate")
@ConditionalOnServerType(ServerType.AUTH_SERVER)
public class AuthenticateController extends BaseAuthServerController {

    @Autowired
    private final UserService userService;

    public AuthenticateController(AuthSessionRepository sessionRepository, TransactionTemplate transactionTemplate, UserService userService) {
        super(sessionRepository, transactionTemplate);
        this.userService = userService;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful authentication",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class),
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = """
                                            {
                                                "user": {
                                                    "id": "a1b2c3d4e5f6",
                                                    "properties": [
                                                        {
                                                            "name": "preferredLanguage",
                                                            "value": "en-us"
                                                        },
                                                        {
                                                            "name": "registrationCountry",
                                                            "value": "US"
                                                        }
                                                    ]
                                                },
                                                "clientToken": "client-identifier-12345",
                                                "accessToken": "a1b2c3d4e5f6g7h8i9j0",
                                                "availableProfiles": [
                                                    {
                                                        "name": "PlayerUsername",
                                                        "id": "a1b2c3d4e5f6a7b8c9d0e1f2g3h4i5j6"
                                                    }
                                                ],
                                                "selectedProfile": {
                                                    "name": "PlayerUsername",
                                                    "id": "a1b2c3d4e5f6a7b8c9d0e1f2g3h4i5j6"
                                                }
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "403", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Invalid Credentials",
                                    value = """
                                            {
                                                "error": "ForbiddenOperationException",
                                                "errorMessage": "Invalid credentials. Invalid username or password."
                                            }
                                            """
                            )))
    })
    @PostMapping
    public ResponseEntity<IErrorResponse> postAuthentication(
            @RequestBody(description = "Authentication request",
                    content = @Content(
                            schema = @Schema(implementation = AuthenticationRequest.class),
                            examples = @ExampleObject(
                                    name = "Authentication Request",
                                    value = """
                                            {
                                                "agent": {
                                                    "name": "Minecraft",
                                                    "version": 1
                                                },
                                                "username": "player@email.example",
                                                "password": "password123",
                                                "clientToken": "client-identifier-12345",
                                                "requestUser": true
                                            }
                                            """
                            )
                    ))
            @org.springframework.web.bind.annotation.RequestBody AuthenticationRequest request) {
        User user;
        try {
            user = userService.login(request.getUsername(), request.getPassword());
        } catch (InvalidCredentialsException e) {
            return YggdrasilError.INVALID_CREDENTIALS;
        }

        UserProfile selectedProfile = new UserProfile(user.getUuid(), user.getUsername());
        String accessToken = UUID.randomUUID().toString();

        if (authSessionRepository.findByEmailAndClientToken(user.getEmail(), request.getClientToken()).isPresent()) {
            return YggdrasilError.FORBIDDEN;
        }

        AuthSession session = new AuthSession();
        session.setUser(user);
        session.setClientToken(request.getClientToken());
        session.setAccessToken(accessToken);
        transactionTemplate.execute(status -> authSessionRepository.save(session));

        List<Property> properties = new ArrayList<>();
        UserProperties userProperties = new UserProperties(user.getUuid().toString(),
                properties);

        AuthenticationResponse authenticationResponse = new AuthenticationResponse(
                accessToken,
                request.getClientToken(),
                selectedProfile,
                List.of(selectedProfile),
                request.isRequestUser() ? userProperties : null
        );

        return ResponseEntity.ok(authenticationResponse);
    }

}