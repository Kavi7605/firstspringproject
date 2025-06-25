package com.java.firstspringproject.controller;

import com.java.firstspringproject.model.CreateUserRequest;
import com.java.firstspringproject.service.UserService;
import com.java.firstspringproject.service.Auth0Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")  // Required to prefix all endpoints with /api
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private Auth0Service auth0Service;

    // Endpoint to create user
    @PostMapping("/create-user")
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest request) {
        // Check if user already exists in local database
        if (userService.isUserPresent(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("⚠️ User already exists in DB.");
        }

        try {
            // Create the user in the local database
            userService.createUser(request);

            // Create the user in Auth0 (without password) and trigger password reset
            auth0Service.createUserInAuth0(request.getEmail());

            // Return success response
            return ResponseEntity.ok("✅ User created: " + request.getEmail());
        } catch (Exception e) {
            // Handle exceptions and return an error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Failed to create user: " + e.getMessage());
        }
    }
}
