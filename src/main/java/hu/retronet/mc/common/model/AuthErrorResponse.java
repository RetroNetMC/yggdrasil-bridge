package hu.retronet.mc.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthErrorResponse implements IErrorResponse {
    private String error;
    private String errorMessage;
    private String cause;
}
