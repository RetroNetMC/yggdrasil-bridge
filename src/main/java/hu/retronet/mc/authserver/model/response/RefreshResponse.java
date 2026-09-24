package hu.retronet.mc.authserver.model.response;

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
public class RefreshResponse extends ErrorResponse {
    private String accessToken;
    private String clientToken;
    private UserProfile selectedProfile;
    private List<UserProfile> availableProfiles = new ArrayList<>();
    private UserProperties user;
}
