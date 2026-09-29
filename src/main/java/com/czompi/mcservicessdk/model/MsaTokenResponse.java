package com.czompi.mcservicessdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MsaTokenResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("id_token")
    private String idToken;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("expires_in")
    private int expiresIn;

    @Getter
    private final LocalDateTime generatedAt = LocalDateTime.now();

    @Getter
    private final LocalDateTime nextRefreshTokenRequestAt = generatedAt.plusDays(30);

}
