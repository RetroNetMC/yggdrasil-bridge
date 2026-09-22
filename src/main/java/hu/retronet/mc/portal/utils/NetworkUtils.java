package hu.retronet.mc.portal.utils;

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

@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class NetworkUtils {

    public static HttpResponse<String> sendGetRequest(String pageUrl, String... headers) {
        HttpClient.newHttpClient();
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

    public static <T> T toJsonObject(HttpResponse<String> response, Class<T> joClass) {
        return new ObjectMapper().readValue(response.body(), joClass);
    }

    public static <T> JsonNode toJsonNode(HttpResponse<T> response) {
        return new ObjectMapper().readTree((String) response.body());
    }

}
