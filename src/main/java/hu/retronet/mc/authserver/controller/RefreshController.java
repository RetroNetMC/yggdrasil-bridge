package hu.retronet.mc.authserver.controller;

import hu.retronet.mc.authserver.model.Property;
import hu.retronet.mc.authserver.model.UserProfile;
import hu.retronet.mc.authserver.model.UserProperties;
import hu.retronet.mc.authserver.model.request.RefreshRequest;
import hu.retronet.mc.authserver.model.response.RefreshResponse;
import hu.retronet.mc.common.BaseAuthServerController;
import hu.retronet.mc.common.conditions.ConditionalOnServerType;
import hu.retronet.mc.common.entity.AuthSession;
import hu.retronet.mc.common.entity.User;
import hu.retronet.mc.common.model.IErrorResponse;
import hu.retronet.mc.common.model.ServerType;
import hu.retronet.mc.common.model.YggdrasilError;
import hu.retronet.mc.common.repository.AuthSessionRepository;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Refresh")
@RestController
@RequestMapping("/refresh")
@ConditionalOnServerType(ServerType.AUTH_SERVER)
public class RefreshController extends BaseAuthServerController {

    public RefreshController(AuthSessionRepository sessionRepository, TransactionTemplate transactionTemplate) {
        super(sessionRepository, transactionTemplate);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RefreshResponse.class),
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = """
                                            {
                                                "accessToken": "new-access-token-a1b2c3d4e5f6",
                                                "clientToken": "client-identifier-12345",
                                                "selectedProfile": {
                                                    "id": "a1b2c3d4e5f6a7b8c9d0e1f2g3h4i5j6",
                                                    "name": "PlayerUsername"
                                                },
                                                "user": {
                                                    "id": "a1b2c3d4e5f6",
                                                    "properties": [
                                                        {
                                                            "name": "preferredLanguage",
                                                            "value": "en"
                                                        },
                                                        {
                                                            "name": "twitch_access_token",
                                                            "value": "twitch-oauth-token"
                                                        }
                                                    ]
                                                }
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "403", description = "Invalid token",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Invalid Token",
                                    value = """
                                            {
                                                "error": "ForbiddenOperationException",
                                                "errorMessage": "Invalid credentials. Invalid username or password."
                                            }
                                            """
                            )))
    })
    @PostMapping
    public ResponseEntity<IErrorResponse> postRefresh(
            @RequestBody(description = "Refresh request",
                    content = @Content(
                            schema = @Schema(implementation = RefreshRequest.class),
                            examples = @ExampleObject(
                                    name = "Refresh Request",
                                    value = """
                                            {
                                                "accessToken": "valid-access-token",
                                                "clientToken": "client-identifier-12345",
                                                "requestUser": true
                                            }
                                            """
                            )
                    ))
            @org.springframework.web.bind.annotation.RequestBody RefreshRequest request) {
        Optional<AuthSession> session = authSessionRepository.findByAccessTokenAndClientToken(request.getAccessToken(), request.getClientToken());

        if (session.isEmpty()) {
            return YggdrasilError.INVALID_CREDENTIALS;
        }

        if(Optional.ofNullable(request.getSelectedProfile()).isPresent()) {
            return YggdrasilError.INVALID_TOKEN;
        }
        User player = session.get().getUser();

        UserProfile selectedProfile = new UserProfile(player.getUuid(), player.getUsername());
        String accessToken = UUID.randomUUID().toString();

        transactionTemplate.execute(status -> authSessionRepository.updateAccessToken(session.get().getUser().getUuid(), session.get().getClientToken(), accessToken));

        List<Property> properties = List.of();
        UserProperties user = new UserProperties(player.getUuid().toString(), properties);

        RefreshResponse response = new RefreshResponse(
                accessToken,
                request.getClientToken(),
                selectedProfile,
                List.of(selectedProfile),
                request.isRequestUser() ? user : null
        );

        return ResponseEntity.ok(response);
    }

}