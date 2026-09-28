package com.czompi.mcservicessdk;

import com.czompi.mcservicessdk.exception.ExternalAuthenticationException;
import com.czompi.mcservicessdk.exception.GameOwnershipException;
import com.czompi.mcservicessdk.exception.GameProfileException;
import com.czompi.mcservicessdk.model.MinecraftAccount;
import com.czompi.mcservicessdk.model.MinecraftGameOwnership;
import com.czompi.mcservicessdk.model.MinecraftToken;
import com.czompi.mcservicessdk.model.MsaTokenResponse;
import com.czompi.mcservicessdk.model.TokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static com.czompi.mcservicessdk.utils.MSAConstants.CLIENT_ID;
import static com.czompi.mcservicessdk.utils.MSAConstants.CLIENT_SECRET;
import static com.czompi.mcservicessdk.utils.MSAConstants.REDIRECT_URI;
import static com.czompi.mcservicessdk.utils.NetworkUtils.sendGetRequest;
import static com.czompi.mcservicessdk.utils.NetworkUtils.sendPostRequest;
import static com.czompi.mcservicessdk.utils.NetworkUtils.toJsonObject;

/**
 * This class provides authentication functionality for the Minecraft services.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAuthentication {

    public static final String MSA_TOKEN_URL = "https://login.microsoftonline.com/consumers/oauth2/v2.0/token";
    public static final String XBL_AUTHENTICATE_URL = "https://user.auth.xboxlive.com/user/authenticate";
    public static final String XSTS_AUTHORIZE_URL = "https://xsts.auth.xboxlive.com/xsts/authorize";
    public static final String MCS_AUTH_URL = "https://api.minecraftservices.com/authentication/login_with_xbox";
    public static final String MCS_ENTITLEMENTS_URL = "https://api.minecraftservices.com/entitlements/mcstore";
    public static final String MCS_ACCOUNT_URL = "https://api.minecraftservices.com/minecraft/profile";
    private static final List<String> MSA_SCOPE = List.of("XboxLive.signin", "offline_access");

    public static MinecraftAccount obtainGameProfile(MsaTokenResponse tokenResponse) throws GameOwnershipException,
            GameProfileException, ExternalAuthenticationException {
        TokenResponse xblResponse = getXblToken(tokenResponse.getAccessToken());

        TokenResponse xstsResponse = getXstsToken(xblResponse);

        MinecraftToken mcResponse = getMinecraftToken(xstsResponse);

        verifyGameOwnership(mcResponse);

        return getMinecraftAccount(mcResponse);
    }

    public static MsaTokenResponse obtainMsaToken(String code, String codeVerifier) throws ExternalAuthenticationException {
        String tokenRequest = getMsaTokenRequest("authorization_code") +
                "&code=" + code +
                "&redirect_uri=" + REDIRECT_URI +
                "&code_verifier=" + codeVerifier;

        HttpResponse<String> response = sendPostRequest(MSA_TOKEN_URL, tokenRequest, "application/x-www-form-urlencoded");
        if (response == null) {
            throw new ExternalAuthenticationException("Failed to acquire tokens.");
        }
        return toJsonObject(response, MsaTokenResponse.class);
    }

    public static MsaTokenResponse refreshMsaToken(String refreshToken) throws ExternalAuthenticationException {
        String tokenRequest = getMsaTokenRequest("refresh_token") +
                "&refresh_token=" + refreshToken;

        HttpResponse<String> response = sendPostRequest(MSA_TOKEN_URL, tokenRequest, "application/x-www-form-urlencoded");
        if (response == null) {
            throw new ExternalAuthenticationException("Failed to acquire tokens.");
        }
        return toJsonObject(response, MsaTokenResponse.class);
    }

    private static @NonNull String getMsaTokenRequest(String grantType) {
        return "grant_type=" + grantType +
                "&scope=" + String.join("%20", MSA_SCOPE) +
                "&client_id=" + CLIENT_ID +
                "&client_secret=" + CLIENT_SECRET;
    }

    // ------- Logic -------

    private static TokenResponse getXblToken(String accessToken) throws ExternalAuthenticationException {
        Map<String, Object> request = Map.of(
                "Properties", Map.of(
                        "AuthMethod", "RPS",
                        "SiteName", "user.auth.xboxlive.com",
                        "RpsTicket", "d=" + accessToken
                ),
                "RelyingParty", "http://auth.xboxlive.com",
                "TokenType", "JWT"
        );
        HttpResponse<String> response = sendPostRequest(XBL_AUTHENTICATE_URL, mapToJsonString(request), "application/json");
        if (response == null) {
            throw new ExternalAuthenticationException("Failed to authenticate with Xbox Live.");
        }
        return toJsonObject(response, TokenResponse.class);
    }

    private static TokenResponse getXstsToken(TokenResponse xblResponse) throws ExternalAuthenticationException {
        Map<String, Object> request = Map.of(
                "Properties", Map.of(
                        "SandboxId", "RETAIL",
                        "UserTokens", List.of(xblResponse.getToken())
                ),
                "RelyingParty", "rp://api.minecraftservices.com/",
                "TokenType", "JWT"
        );
        HttpResponse<String> response = sendPostRequest(XSTS_AUTHORIZE_URL, mapToJsonString(request), "application/json");
        if (response == null) {
            throw new ExternalAuthenticationException("Failed to obtain XSTS Token.");
        }
        return toJsonObject(response, TokenResponse.class);
    }

    private static String mapToJsonString(Object request) {
        try {
            return new ObjectMapper().writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static MinecraftToken getMinecraftToken(TokenResponse xstsResponse) throws ExternalAuthenticationException {
        String userHash = xstsResponse.getDisplayClaims().getXui().get(0).getUserHash();
        String xstsToken = xstsResponse.getToken();
        Map<String, String> request = Map.of(
                "identityToken", "XBL3.0 x=" + userHash + ";" + xstsToken
        );
        HttpResponse<String> response = sendPostRequest(MCS_AUTH_URL, mapToJsonString(request), "application/json");
        if (response == null) {
            throw new ExternalAuthenticationException("Failed to login to Minecraft Account.");
        }
        return toJsonObject(response, MinecraftToken.class);
    }

    /// Verifies that the user owns the game
    ///
    /// @param mcResponse
    /// @throws GameOwnershipException When the user does not own the game or the request fails
    private static void verifyGameOwnership(MinecraftToken mcResponse) throws GameOwnershipException, ExternalAuthenticationException {
        HttpResponse<String> response = sendGetRequest(MCS_ENTITLEMENTS_URL, "Authorization", "Bearer " + mcResponse.getAccessToken());
        if (response == null || response.statusCode() != 200) {
            throw new GameOwnershipException("Failed to verify game ownership. Endpoint returned invalid response.");
        }

        MinecraftGameOwnership jsonResponse = toJsonObject(response, MinecraftGameOwnership.class);

        if (jsonResponse == null || jsonResponse.getItems() == null) {
            throw new GameOwnershipException("Failed to verify game ownership. Response returned empty.");
        }

        if (jsonResponse.getItems().stream().noneMatch(item -> item.getName().equals("product_minecraft") || item.getName().equals("game_minecraft"))) {
            throw new GameOwnershipException("User does not own Minecraft.");
        }

        // If we reach here, the user owns Minecraft
        log.debug("User owns Minecraft. Ownership verified.");
    }

    private static MinecraftAccount getMinecraftAccount(MinecraftToken mcResponse) throws GameProfileException, ExternalAuthenticationException {
        HttpResponse<String> response = sendGetRequest(MCS_ACCOUNT_URL, "Authorization", "Bearer " + mcResponse.getAccessToken());
        if (response == null) {
            log.error("Failed to get Minecraft Account information.");
            throw new GameProfileException("Failed to get Minecraft Account information.");
        }
        return toJsonObject(response, MinecraftAccount.class);
    }

}
