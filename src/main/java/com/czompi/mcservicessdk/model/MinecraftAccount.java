package com.czompi.mcservicessdk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import hu.retronet.mc.common.json_deserializer.UuidDeserializer;
import lombok.Data;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/// GET [...](https://api.minecraftservices.com/minecraft/profile)
/// ```json
///{
///     "id": "986dec87b7ec47ff89ff033fdb95c4b5",
///     "name": "HowDoesAuthWork",
///     "skins": [
///         {
///             "id": "6a6e65e5-76dd-4c3c-a625-162924514568",
///             "state": "ACTIVE",
///             "url": "http://textures.minecraft.net/texture/1a4af718455d4aab528e7a61f86fa25e6a369d1768dcb13f7df319a713eb810b",
///             "variant": "CLASSIC",
///             "alias": "STEVE"
///         }
///     ],
///     "capes": [
///         {
///             "id": "5af20372-79e0-4e1f-80f8-6bd8e3135995",
///             "state": "ACTIVE",
///             "url": "http://textures.minecraft.net/texture/2340c0e03dd24a11b15a8b33c2a7e9e32abb2051b2481d0ba7defd635ca7a933",
///             "alias": "Migrator"
///         }
///     ]
/// }
/// ```
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

