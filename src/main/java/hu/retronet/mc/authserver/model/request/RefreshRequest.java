package hu.retronet.mc.authserver.model.request;

import com.czompi.mcservicessdk.model.MinecraftProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshRequest {
    private final String clientToken;
    private final String accessToken;
    private final MinecraftProfile selectedProfile;
    private final boolean requestUser = true;
}
