package com.java.firstspringproject.controller;

import com.java.firstspringproject.model.CreateUserRequest;
import com.java.firstspringproject.model.User;
import com.java.firstspringproject.repository.UserRepository;
import com.java.firstspringproject.service.Auth0Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private final Auth0Service auth0Service;
    private final UserRepository userRepository;

    public UserRestController(Auth0Service auth0Service, UserRepository userRepository) {
        this.auth0Service = auth0Service;
        this.userRepository = userRepository;
    }
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }


    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/ui";
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest request) {
        try {
            String auth0Id = auth0Service.createAuth0User(
                    request.getEmail(),
                    request.getName(),
                    request.getPassword()
            );

            User user = new User(auth0Id, request.getEmail(), request.getName());
            userRepository.save(user);

            return ResponseEntity.ok("User created with ID: " + auth0Id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInUser(@AuthenticationPrincipal Jwt jwt) {
        String auth0UserId = jwt.getSubject(); // e.g. auth0|abc123

        return userRepository.findById(auth0UserId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found in local DB"));
    }


}
