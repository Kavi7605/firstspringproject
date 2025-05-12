package com.java.firstspringproject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class Auth0Service {

    @Value("${auth0.mgmt.client-id}")
    private String clientId;

    @Value("${auth0.mgmt.client-secret}")
    private String clientSecret;

    @Value("${auth0.mgmt.audience}")
    private String audience;

    @Value("${auth0.mgmt.domain}")
    private String domain;

    private String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payload = Map.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                "audience", audience,
                "grant_type", "client_credentials"
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);
        Map<String, Object> response = restTemplate.postForObject("https://" + domain + "/oauth/token", request, Map.class);
        return (String) response.get("access_token");
    }

    public String createAuth0User(String email, String name, String password) {
        String token = getAccessToken();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> user = Map.of(
                "email", email,
                "name", name,
                "connection", "Username-Password-Authentication",
                "password", password
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(user, headers);
        Map<String, Object> response = restTemplate.postForObject("https://" + domain + "/api/v2/users", entity, Map.class);
        return (String) response.get("user_id");  // e.g. auth0|abc123
    }
}
