package hu.retronet.mc.authserver.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class AuthenticationRequest {

    private final Agent agent;

    private final String username;

    private final String password;

    private final String clientToken;

    private final boolean requestUser = true;

    @Data
    @AllArgsConstructor
    public static final class Agent {
        private final String name;
        private final int version;
    }
}