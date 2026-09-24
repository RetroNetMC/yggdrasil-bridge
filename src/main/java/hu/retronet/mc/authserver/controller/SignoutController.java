package hu.retronet.mc.authserver.controller;

import hu.retronet.mc.authserver.model.request.SignoutRequest;
import hu.retronet.mc.common.BaseAuthServerController;
import hu.retronet.mc.common.conditions.ConditionalOnServerType;
import hu.retronet.mc.common.model.ServerType;
import hu.retronet.mc.common.repository.AuthSessionRepository;
import hu.retronet.mc.common.service.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Signout")
@RestController
@RequestMapping("/signout")
@ConditionalOnServerType(ServerType.AUTH_SERVER)
public class SignoutController extends BaseAuthServerController {

    private final UserService userService;

    public SignoutController(AuthSessionRepository sessionRepository, TransactionTemplate transactionTemplate, UserService userService) {
        super(sessionRepository, transactionTemplate);
        this.userService = userService;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signout successful")
    })
    @PostMapping
    public ResponseEntity<Void> postSignout(
            @RequestBody(description = "Signout request",
                    content = @Content(
                            schema = @Schema(implementation = SignoutRequest.class),
                            examples = @ExampleObject(
                                    name = "Signout Request",
                                    description = "Example of a signout request",
                                    value = """
                                            {
                                                "username": "player@email.example",
                                                "password": "password123"
                                            }
                                            """
                            )
                    ))
            @org.springframework.web.bind.annotation.RequestBody SignoutRequest request) {
        boolean result = userService.logout(request.getUsername(), request.getPassword());
        if (result) {
            return ResponseEntity.status(HttpStatus.OK)
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .build();
        }
    }

}