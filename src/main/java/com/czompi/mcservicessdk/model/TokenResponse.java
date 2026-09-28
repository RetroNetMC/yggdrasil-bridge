package com.czompi.mcservicessdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TokenResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    @JsonProperty("IssueInstant")
    private LocalDateTime issueInstant;

    @JsonProperty("NotAfter")
    private LocalDateTime notAfter;

    @JsonProperty("Token")
    private String token;

    @JsonProperty("DisplayClaims")
    private DisplayClaims displayClaims;

    @Data
    public static class DisplayClaims implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonProperty("xui")
        private List<Xui> xui = new ArrayList<>();
    }

    @Data
    public static class Xui implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonProperty("uhs")
        private String userHash;

        @JsonProperty("xid")
        private String userId;

    }
}
