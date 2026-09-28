package hu.retronet.mc.common.model;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

import static com.czompi.mcservicessdk.utils.MSAConstants.BASE_URL;

@Getter
@ToString
public class YggdrasilError {
    /// An attempt to send a POST request with incorrect request headers to any endpoint.
    public static ResponseEntity<IErrorResponse> UNSUPPORTED_MEDIA_TYPE = errorResponse(new ErrorResponse("Unsupported Media Type", null, "The server is refusing to service the request because the entity of the request is in a format not supported by the requested resource for the requested method", 415, null, null));

    /// An attempt to use a request method other than POST to access any of the endpoints.
    public static ResponseEntity<IErrorResponse> METHOD_NOT_ALLOWED = errorResponse(new ErrorResponse("Method Not Allowed", null, "The method specified in the request is not allowed for the resource identified by the request URI", 405, null, null));

    /// An attempt to send a request to a non-existent endpoint.
    public static ResponseEntity<IErrorResponse> NOT_FOUND = errorResponse(new ErrorResponse("Not Found", null, "The server has not found anything matching the request URI", 404, null, null));

    /// A successful attempt to sign in using a migrated Mojang account.
    public static ResponseEntity<IErrorResponse> MIGRATED = errorResponse(new ErrorResponse("GoneException", null, "Migrated", 410, null, null));

    /// An attempt to sign in using empty or insufficiently short credentials.
    public static ResponseEntity<IErrorResponse> FORBIDDEN = errorResponse(new ErrorResponse("ForbiddenOperationException", null, "Forbidden", 403, null, null));

    /// Either a successful attempt to sign in using an account with excessive login attempts or an unsuccessful attempt to sign in using a non-existent account.
    public static ResponseEntity<IErrorResponse> INVALID_CREDENTIALS = errorResponse(new ErrorResponse("ForbiddenOperationException", null, "Invalid credentials. Invalid username or password.", 403, null, null));

    /// An unsuccessful attempt to sign in using a Legacy account without a valid Minecraft purchase.
    public static ResponseEntity<IErrorResponse> LEGACY_FREE_ACCOUNT = errorResponse(new ErrorResponse("ForbiddenOperationException", null, "Invalid credentials. Legacy account is non-premium account.", 403, null, null));

    /// An unsuccessful attempt to sign in using a migrated Legacy account.
    public static ResponseEntity<IErrorResponse> MIGRATED_USE_USERNAME = errorResponse(new ErrorResponse("ForbiddenOperationException", "UserMigratedException", "Invalid credentials. Account migrated, use email as username.", 403, null, null));

    /// An attempt to refresh an access token that has been invalidated, no longer exists, or has been erased.
    public static ResponseEntity<IErrorResponse> TOKEN_DOES_NOT_EXIST = errorResponse(new ErrorResponse("TooManyRequestsException", null, "Token does not exist", 403, null, null));

    /// An attempt to validate an access token obtained from the `/authenticate` endpoint that has expired or become invalid.
    public static ResponseEntity<IErrorResponse> INVALID_TOKEN = errorResponse(new ErrorResponse("ForbiddenOperationException", null, "Invalid token", 403, null, null));

    /// An attempt to validate an access token obtained from the `/authenticate` endpoint that has expired or become invalid while under rate-limiting conditions.
    public static ResponseEntity<IErrorResponse> INVALID_TOKEN_RATE_LIMITED = errorResponse(new ErrorResponse("ForbiddenOperationException", null, "Invalid token.", 429, null, null));

    /// An attempt to validate an access token obtained from the `/authenticate` endpoint that has expired or become invalid while under rate-limiting conditions.
    public static ResponseEntity<IErrorResponse> INVALID_MSA_REFRESH_TOKEN = errorResponse(new ErrorResponse("ForbiddenOperationException", "Your Microsoft-session has expired. Please visit '" + BASE_URL + "' website to re-authenticate!", "Microsoft refresh token invalidated", 429, null, null));

    public static ResponseEntity<IErrorResponse> getGenericError(Integer status) {
        return errorResponse(new ErrorResponse("UnexpectedError", null, "An unexpected error occurred", status, null, null));
    }
    private static ResponseEntity<IErrorResponse> errorResponse(ErrorResponse error) {
        return ResponseEntity.status(error.getStatusCode()).body(error);
    }

}
