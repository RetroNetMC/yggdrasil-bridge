package hu.retronet.mc.authserver.model.request;

import hu.retronet.mc.authserver.model.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshRequest {
    private final String clientToken;
    private final String accessToken;
    private final UserProfile selectedProfile;
    private final boolean requestUser = true;
}
