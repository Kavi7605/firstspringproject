package com.java.firstspringproject.service;

import com.java.firstspringproject.config.Auth0Properties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Service
public class Auth0Service {
    private final WebClient webClient;
    private final Auth0Properties auth0Properties;

    public Auth0Service(WebClient.Builder webClientBuilder, Auth0Properties auth0Properties) {
        this.webClient = webClientBuilder.build();
        this.auth0Properties = auth0Properties;
    }

    private String getManagementApiToken() {
        Map<String, String> request = Map.of(
                "grant_type", "client_credentials",
                "client_id", auth0Properties.getClientId(),
                "client_secret", auth0Properties.getClientSecret(),
                "audience", auth0Properties.getAudience()
        );

        return webClient.post()
                .uri("https://" + auth0Properties.getDomain() + "/oauth/token")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .map(token -> (String) token.get("access_token"))
                .block();
    }

    // Create user without password
    public void createUserInAuth0(String email) {
        String token = getManagementApiToken();

        // Request body to create user without password
        Map<String, Object> requestBody = Map.of(
                "email", email,
                "connection", "Username-Password-Authentication" // Specify Auth0 connection
        );

        webClient.post()
                .uri("https://" + auth0Properties.getDomain() + "/api/v2/users")
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> System.err.println("Auth0 creation error: " + error.getMessage()))
                .doOnSuccess(response -> {
                    System.out.println("Auth0 user created: " + response);
                    // After creating the user, trigger password reset
                    triggerPasswordReset(email);
                })
                .subscribe();
    }

    // Trigger password reset email
    public void triggerPasswordReset(String userEmail) {
        String token = getManagementApiToken();

        // Request body to initiate password reset
        Map<String, Object> resetRequestBody = Map.of(
                "client_id", auth0Properties.getClientId(),
                "email", userEmail,
                "connection", "Username-Password-Authentication"
        );

        webClient.post()
                .uri("https://" + auth0Properties.getDomain() + "/api/v2/tickets/password-change")
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(resetRequestBody)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> System.err.println("Password reset error: " + error.getMessage()))
                .doOnSuccess(response -> {
                    System.out.println("Password reset email sent: " + response);
                })
                .subscribe();
    }
}