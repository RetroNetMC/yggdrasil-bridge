package hu.retronet.mc.authserver.controller;

import hu.retronet.mc.authserver.model.request.ValidateRequest;
import hu.retronet.mc.common.BaseAuthServerController;
import hu.retronet.mc.common.conditions.ConditionalOnServerType;
import hu.retronet.mc.common.entity.AuthSession;
import hu.retronet.mc.common.model.IErrorResponse;
import hu.retronet.mc.common.model.ServerType;
import hu.retronet.mc.common.repository.AuthSessionRepository;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Tag(name = "Validate")
@Controller
@RequestMapping("/validate")
@ConditionalOnServerType(ServerType.AUTH_SERVER)
public class ValidateController extends BaseAuthServerController {

    public ValidateController(AuthSessionRepository sessionRepository, TransactionTemplate transactionTemplate) {
        super(sessionRepository, transactionTemplate);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Token is valid"),
            @ApiResponse(responseCode = "403", description = "Token is invalid")
    })
    @PostMapping
    public ResponseEntity<IErrorResponse> postValidate(
            @RequestBody(description = "Validate request",
                    content = @Content(
                            schema = @Schema(implementation = ValidateRequest.class),
                            examples = @ExampleObject(
                                    name = "Validate Request",
                                    value = """
                                            {
                                                "accessToken": "valid-access-token",
                                                "clientToken": "client-identifier-12345"
                                            }
                                            """
                            )
                    ))
            @org.springframework.web.bind.annotation.RequestBody ValidateRequest request) {
        Optional<AuthSession> session = authSessionRepository.findByAccessTokenAndClientToken(request.getAccessToken(), request.getClientToken());
        if (session.isPresent() && isValidSession(session.get())) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else  {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

    }

}