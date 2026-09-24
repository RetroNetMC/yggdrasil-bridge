package hu.retronet.mc.authserver.controller;

import hu.retronet.mc.authserver.model.request.ValidateRequest;
import hu.retronet.mc.common.BaseAuthServerController;
import hu.retronet.mc.common.conditions.ConditionalOnServerType;
import hu.retronet.mc.common.entity.AuthSession;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Tag(name = "Invalidate")
@RestController
@RequestMapping("/invalidate")
@ConditionalOnServerType(ServerType.AUTH_SERVER)
public class InvalidateController extends BaseAuthServerController {

    public InvalidateController(AuthSessionRepository sessionRepository, TransactionTemplate transactionTemplate) {
        super(sessionRepository, transactionTemplate);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Token invalidated successfully"),
            @ApiResponse(responseCode = "403", description = "Invalid token",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Invalid Token",
                                    value = """
                                            {
                                                "error": "ForbiddenOperationException",
                                                "errorMessage": "Invalid token"
                                            }
                                            """
                            )))
    })
    @PostMapping
    public ResponseEntity<IErrorResponse> postInvalidate(
            @RequestBody(description = "Invalidate request",
                    content = @Content(
                            schema = @Schema(implementation = ValidateRequest.class),
                            examples = @ExampleObject(
                                    name = "Invalidate Request",
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
        if (session.isPresent()) {
            transactionTemplate.executeWithoutResult(status -> authSessionRepository.delete(session.get()));
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else  {
            return YggdrasilError.INVALID_TOKEN;
        }

    }

}