package hu.retronet.mc.authserver.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidateRequest {
    private String clientToken;
    private String accessToken;
}
