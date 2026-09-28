package com.czompi.mcservicessdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Texture implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    @JsonProperty("id")
    private String id;

    @JsonProperty("state")
    private String state;

    @JsonProperty("url")
    private String url;

    @JsonProperty("variant")
    private String variant;

    @JsonProperty("alias")
    private String alias;

}
