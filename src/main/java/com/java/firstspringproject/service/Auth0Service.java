package com.java.firstspringproject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class Auth0Service {

    private final WebClient webClient;
    private final String domain;
    private final String clientId;
    private final String clientSecret;
    private final String audience;

    public Auth0Service(
            WebClient.Builder webClientBuilder,
            @Value("${auth0.mgmt.domain}") String domain,
            @Value("${auth0.mgmt.client-id}") String clientId,
            @Value("${auth0.mgmt.client-secret}") String clientSecret,
            @Value("${auth0.mgmt.audience}") String audience) {

        this.webClient = webClientBuilder.baseUrl("https://" + domain).build();
        this.domain = domain;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.audience = audience;
    }

    public void createUserInAuth0(String email, String name, String password, String phoneNumber) {
        if (!phoneNumber.matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone number must be exactly 10 digits.");
        }

        String formattedPhone = "+91" + phoneNumber;

        String token = getAccessToken();

        Map<String, Object> userMetadata = new HashMap<>();
        userMetadata.put("phone_number", formattedPhone);

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("email", email);
        userPayload.put("name", name);
        userPayload.put("password", password);
        userPayload.put("connection", "Username-Password-Authentication");
        userPayload.put("user_metadata", userMetadata);  // ✅ Correctly nested metadata

        // Debug log for Auth0 payload
        System.out.println("Sending to Auth0: " + userPayload);

        webClient.post()
                .uri("/api/v2/users")
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(userPayload)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // You can improve with proper error handling or logging
    }

    private String getAccessToken() {
        Map<String, String> tokenRequest = new HashMap<>();
        tokenRequest.put("grant_type", "client_credentials");
        tokenRequest.put("client_id", clientId);
        tokenRequest.put("client_secret", clientSecret);
        tokenRequest.put("audience", audience);

        Map<String, Object> response = webClient.post()
                .uri("/oauth/token")
                .bodyValue(tokenRequest)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || !response.containsKey("access_token")) {
            throw new RuntimeException("Failed to retrieve access token from Auth0.");
        }

        return response.get("access_token").toString();
    }
}
