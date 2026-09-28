package hu.retronet.mc.authserver.model.response;

import com.czompi.mcservicessdk.model.MinecraftProfile;
import com.fasterxml.jackson.annotation.JsonInclude;
import hu.retronet.mc.authserver.model.UserProperties;
import hu.retronet.mc.common.model.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse extends ErrorResponse {

    private String accessToken;

    private String clientToken;

    private MinecraftProfile selectedProfile;

    private List<MinecraftProfile> availableProfiles = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserProperties user;

}
