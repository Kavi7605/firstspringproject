package com.java.firstspringproject.service;

import com.java.firstspringproject.config.Auth0Properties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.json.mgmt.users.UsersEntity;
import com.auth0.json.mgmt.users.User;

@Service
public class Auth0Service {
    private final WebClient webClient;
    private final Auth0Properties auth0Properties;

    // Constructor to initialize WebClient and Auth0Properties
    public Auth0Service(WebClient.Builder webClientBuilder, Auth0Properties auth0Properties) {
        this.webClient = webClientBuilder.build();
        this.auth0Properties = auth0Properties;
    }

    // Method to get the management API token (OAuth client credentials flow)
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

    // Method to create a user in Auth0 (without a password)
    public void createUserInAuth0(String email) {
        String token = getManagementApiToken();

        // Prepare the request body with email and connection (no password)
        Map<String, Object> requestBody = Map.of(
                "email", email,
                "connection", "Username-Password-Authentication"
        );

        webClient.post()
                .uri("https://" + auth0Properties.getDomain() + "/api/v2/users")
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> System.err.println("Auth0 creation error: " + error.getMessage()))
                .subscribe(response -> {
                    System.out.println("Auth0 user created: " + response);
                    // After user creation, trigger the password reset email
                    triggerPasswordReset(email);
                });
    }

    // Method to trigger a password reset email for the user
    public void triggerPasswordReset(String userEmail) {
        String token = getManagementApiToken();

        // Fetch user by email
        UserFilter filter = new UserFilter().withEmail(userEmail);

        try {
            // Get the user data from Auth0
            ManagementAPI api = new ManagementAPI("https://" + auth0Properties.getDomain(), token);
            UsersEntity users = api.users().list(filter).execute();

            if (users.getTotal() > 0) {
                User user = users.getItems().get(0);  // Get the first user (since email is unique)
                // Send password reset email
                api.getEmailProvider().sendPasswordResetEmail(user.getId());
                System.out.println("Password reset email sent to " + userEmail);
            } else {
                System.out.println("User not found with email: " + userEmail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error triggering password reset: " + e.getMessage());
        }
    }
}
