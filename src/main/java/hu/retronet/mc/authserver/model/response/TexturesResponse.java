package hu.retronet.mc.authserver.model.response;

import hu.retronet.mc.authserver.model.TextureInfo;
import hu.retronet.mc.common.model.TextureType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TexturesResponse {
    private long timestamp;
    private UUID profileId;
    private String profileName;
    private boolean isPublic;
    private Map<TextureType, TextureInfo> textures = new EnumMap<>(TextureType.class);

}
