package hu.retronet.mc.authserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TextureInfo {
    private String url;
    private String hash;
    private Map<String, String> metadata = new HashMap<>();
}
