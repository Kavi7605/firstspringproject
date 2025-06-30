package com.java.firstspringproject.service;

import com.java.firstspringproject.model.CreateUserRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
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
                "password", "TempPass@12345",  // <- required field for this connection
                "email_verified", false
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        return (String) response.getBody().get("user_id");
    }

    public void sendPasswordResetEmail(String auth0UserId) {
        ensureManagementToken();

        String url = "https://" + domain + "/api/v2/tickets/password-change";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(managementToken);

        Map<String, Object> body = Map.of("user_id", auth0UserId);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(url, request, Map.class);
    }
}
