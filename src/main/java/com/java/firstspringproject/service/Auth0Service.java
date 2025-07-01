package com.java.firstspringproject.service;

import com.java.firstspringproject.model.CreateUserRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class Auth0Service {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${auth0.mgmt.domain}")
    private String domain;

    @Value("${auth0.mgmt.client-id}")
    private String clientId;

    @Value("${auth0.mgmt.client-secret}")
    private String clientSecret;

    @Value("${auth0.mgmt.audience}")
    private String audience;

    private String managementToken;

    private void ensureManagementToken() {
        if (managementToken != null) return;

        String tokenUrl = "https://" + domain + "/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "grant_type", "client_credentials",
                "client_id", clientId,
                "client_secret", clientSecret,
                "audience", audience
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        managementToken = (String) response.getBody().get("access_token");
    }

    public String createUserWithoutPassword(CreateUserRequest req) {
        ensureManagementToken();

        String url = "https://" + domain + "/api/v2/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(managementToken);

        Map<String, Object> body = Map.of(
                "connection", "Username-Password-Authentication",
                "email", req.getEmail(),
                "name", req.getName(),
                "phone_number", req.getPhoneNumber(),
                "password", "TempPass@12345",
                "email_verified", true
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            String userId = (String) response.getBody().get("user_id");

            sendPasswordResetEmail(req.getEmail());
            return userId;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                throw new RuntimeException("❌ Auth0 user already exists for this email/phone.");
            }
            throw e;
        }
    }

    public void sendPasswordResetEmail(String email) {
        String url = "https://" + domain + "/dbconnections/change_password";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "client_id", clientId,
                "email", email,
                "connection", "Username-Password-Authentication"
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, request, String.class);
    }
}
