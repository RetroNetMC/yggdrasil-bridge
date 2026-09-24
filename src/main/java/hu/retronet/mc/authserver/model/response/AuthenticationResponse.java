package hu.retronet.mc.authserver.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import hu.retronet.mc.authserver.model.UserProfile;
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

    private UserProfile selectedProfile;

    private List<UserProfile> availableProfiles = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserProperties user;

}
