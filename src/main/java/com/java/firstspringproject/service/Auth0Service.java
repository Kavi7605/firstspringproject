// src/main/java/com/java/firstspringproject/service/Auth0Service.java
package com.java.firstspringproject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Service
public class Auth0Service {
    private final WebClient webClient;

    @Value("${auth0.mgmt.domain}")
    private String domain;

    @Value("${auth0.mgmt.client-id}")
    private String clientId;

    @Value("${auth0.mgmt.client-secret}")
    private String clientSecret;

    @Value("${auth0.mgmt.audience}")
    private String audience;

    public Auth0Service(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    private String getManagementApiToken() {
        Map<String, String> request = Map.of(
                "grant_type", "client_credentials",
                "client_id", clientId,
                "client_secret", clientSecret,
                "audience", audience
        );

        return webClient.post()
                .uri("https://" + domain + "/oauth/token")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .map(token -> (String) token.get("access_token"))
                .block();
    }

    public void createUserInAuth0(String email, String password) {
        String token = getManagementApiToken();

        Map<String, Object> requestBody = Map.of(
                "email", email,
                "password", password,
                "connection", "Username-Password-Authentication"
        );

        webClient.post()
                .uri("https://" + domain + "/api/v2/users")
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> System.err.println("Auth0 creation error: " + error.getMessage()))
                .subscribe(response -> System.out.println("Auth0 user created: " + response));
    }
}
