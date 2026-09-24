package hu.retronet.mc.authserver.model.request;

import hu.retronet.mc.authserver.model.Agent;
import lombok.AllArgsConstructor;
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

}