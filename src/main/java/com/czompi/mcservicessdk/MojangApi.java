package com.czompi.mcservicessdk;

import com.czompi.mcservicessdk.exception.ExternalAuthenticationException;
import com.czompi.mcservicessdk.exception.GameProfileException;
import com.czompi.mcservicessdk.model.MinecraftAccount;
import com.czompi.mcservicessdk.model.MinecraftProfile;
import com.czompi.mcservicessdk.utils.NetworkUtils;

import java.util.UUID;

import static com.czompi.mcservicessdk.utils.NetworkUtils.toJsonObject;

/**
 * This class covers Mojang public API endpoints.
 * <a href="https://discovery.minecraftservices.com/minecraft/client">https://discovery.minecraftservices.com/minecraft/client</a>
 */
public class MojangApi {
    public static final String BASE_URL = "https://api.minecraftservices.com";
    public static final String PROFILE_BY_NAME_URL = BASE_URL + "/minecraft/profile/lookup/name/%s";
    public static final String PROFILE_BY_ID_URL = BASE_URL + "/minecraft/profile/lookup/%s";

    public static MinecraftProfile getProfileById(UUID uuid) throws GameProfileException {

        var response = NetworkUtils.sendGetRequest(String.format(PROFILE_BY_ID_URL, uuid.toString().replace("-", "")));
        try {
            return toJsonObject(response, MinecraftProfile.class);
        } catch (ExternalAuthenticationException e) {
            throw new GameProfileException("Failed to retrieve Minecraft profile by ID: " + uuid);
        }
    }


}
