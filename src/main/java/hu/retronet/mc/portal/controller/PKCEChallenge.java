package hu.retronet.mc.portal.controller;

import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

@Getter
public class PKCEChallenge {
    private final String codeVerifier;
    private final String codeChallenge;

    public PKCEChallenge() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] codeVerifierBytes = new byte[32];
        secureRandom.nextBytes(codeVerifierBytes);

        String codeVerifier = Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifierBytes);

        // MUST be URL-safe Base64 with NO padding
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] signature = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
        String codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(signature);

        this.codeVerifier = codeVerifier;
        this.codeChallenge = codeChallenge;
    }

    public PKCEChallenge(String codeVerifier, String codeChallenge) {
        this.codeVerifier = Objects.requireNonNull(codeVerifier);
        this.codeChallenge = Objects.requireNonNull(codeChallenge);
    }

    public static PKCEChallenge generate() {
        return new PKCEChallenge();
    }

    public static String generateCodeVerifier() {
        return new PKCEChallenge().getCodeVerifier();
    }
}
