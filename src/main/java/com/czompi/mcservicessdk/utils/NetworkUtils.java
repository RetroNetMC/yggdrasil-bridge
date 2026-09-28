package com.czompi.mcservicessdk.utils;

import com.czompi.mcservicessdk.exception.ExternalAuthenticationException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class NetworkUtils {

    public static HttpResponse<String> sendGetRequest(String pageUrl, String... headers) {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(pageUrl))
                .headers(headers)
                .GET()
                .build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.error("Error occurred during authorization.", e);
        } catch (InterruptedException e) {
            log.warn("Interrupted while waiting for response.", e);
        }
        return null;
    }

    public static HttpResponse<String> sendPostRequest(String pageUrl, String postData, String contentType, String... headers) {
        if (contentType != null && !contentType.isEmpty() && Arrays.stream(headers).noneMatch(h -> h.equalsIgnoreCase("Content-Type"))) {
            String[] newHeaders = new String[headers.length + 2];
            System.arraycopy(headers, 0, newHeaders, 0, headers.length);
            newHeaders[headers.length] = "Content-Type";
            newHeaders[headers.length + 1] = contentType;
            headers = newHeaders;
        }
        if (Arrays.stream(headers).noneMatch(h -> h.equalsIgnoreCase("Accept"))) {
            String[] newHeaders = new String[headers.length + 2];
            System.arraycopy(headers, 0, newHeaders, 0, headers.length);
            newHeaders[headers.length] = "Accept";
            newHeaders[headers.length + 1] = "application/json";
            headers = newHeaders;
        }
        HttpClient.newHttpClient();
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(pageUrl))
                .headers(headers)
                .POST(HttpRequest.BodyPublishers.ofString(postData))
                .build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.error("Error occurred during authorization.", e);
        } catch (InterruptedException e) {
            log.warn("Interrupted while waiting for response.", e);
        }
        return null;
    }

    public static <T> T toJsonObject(HttpResponse<String> response, Class<T> joClass) throws ExternalAuthenticationException {
        var returnNode = toJsonNode(response);
        if (returnNode == null) {
            return null;
        }
        if (returnNode.has("error_message") || returnNode.has("errorMessage") ||
                returnNode.has("error_description") || returnNode.has("XError")) {
            String errorMessage;
            if (returnNode.has("error_message")) {
                errorMessage = returnNode.get("error_message").asString();
            } else if (returnNode.has("errorMessage")) {
                errorMessage = returnNode.get("errorMessage").asString();
            } else if (returnNode.has("error_description")) {
                errorMessage = returnNode.get("error_description").asString();
            } else if (returnNode.has("XError")) {
                errorMessage = translateXError(returnNode.get("XError").asLong());
            } else {
                log.error("Error occurred during authorization: {}", response.body());
                throw new ExternalAuthenticationException("An unhandled exception occurred during authorization. " +
                        "Contact administrator with the following timestamp: " + System.currentTimeMillis());
            }

            throw new ExternalAuthenticationException("Error occurred during authorization: " + errorMessage);
        }
        log.debug("Response body: {}", response.body());

        return new ObjectMapper().readValue(response.body(), joClass);
    }

    private static String translateXError(Long xError) {
        return Map.of(
                2148916227L, "The account is banned from Xbox.",
                // This should not happen, as we are authenticating with an existing account.
                2148916233L, "The account doesn't have an Xbox account.",
                2148916235L, "The account is from a country where Xbox Live is not available/banned",
                2148916236L, "The account needs adult verification on Xbox page. (South Korea)",
                2148916237L, "The account needs adult verification on Xbox page. (South Korea)",
                2148916238L, "The account is a child (under 18) and cannot proceed unless the account is added to a Family by an adult.",
                2148916262L, "Token parsing error. The token is invalid or expired."
        ).getOrDefault(xError, "Unknown XError: " + xError);
    }

    public static JsonNode toJsonNode(HttpResponse<String> response) {
        if (response == null) {
            log.error("Response is null.");
            return null;
        }
        return new ObjectMapper().readTree(response.body());
    }

}
