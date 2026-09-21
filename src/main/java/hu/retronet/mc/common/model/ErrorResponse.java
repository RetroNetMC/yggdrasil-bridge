package hu.retronet.mc.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.OptBoolean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@AllArgsConstructor
public class ErrorResponse implements Serializable, IErrorResponse {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("error")
    private final String error;

    @JsonProperty(value = "cause", isRequired = OptBoolean.FALSE)
    private String cause;

    @JsonProperty("errorMessage")
    private final String errorMessage;

    @JsonProperty(value = "status", isRequired = OptBoolean.FALSE)
    private Integer statusCode;

    @JsonProperty(value = "path", isRequired = OptBoolean.FALSE)
    private String path;

    @JsonProperty(value = "timestamp", isRequired = OptBoolean.FALSE)
    private LocalDateTime timestamp;

    public ErrorResponse(String error, String cause, String errorMessage) {
        this(error, cause, errorMessage, LocalDateTime.now());
    }

    public ErrorResponse(String error, String errorMessage, LocalDateTime timestamp) {
        this(error, null, errorMessage, null, null, timestamp);
    }

    public ErrorResponse(String error, String cause, String errorMessage, LocalDateTime timestamp) {
        this(error, cause, errorMessage, null, null, timestamp);
    }

}
