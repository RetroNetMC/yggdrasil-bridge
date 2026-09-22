package hu.retronet.mc.portal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import hu.retronet.mc.common.json_deserializer.UuidDeserializer;
import lombok.Data;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinecraftAccount implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    @JsonDeserialize(using = UuidDeserializer.class)
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("skins")
    private Texture[] skins;

    @JsonProperty("capes")
    private Texture[] capes;
}

