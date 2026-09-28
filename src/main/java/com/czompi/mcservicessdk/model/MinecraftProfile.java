package com.czompi.mcservicessdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class MinecraftProfile {
    @JsonProperty(value = "id", required = true)
    private static UUID id;

    @JsonProperty(value = "name", required = true)
    private static String name;
}
