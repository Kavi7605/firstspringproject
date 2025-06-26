package com.java.firstspringproject.controller;

import com.java.firstspringproject.model.CreateUserRequest;
import com.java.firstspringproject.service.Auth0Service;
import com.java.firstspringproject.service.UserService;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)  // Required for Mockito annotations to work
public class UserRestControllerTest {

    @InjectMocks
    private UserRestController userRestController;  // Inject mocked services into the controller

    @Mock
    private UserService userService;  // Mock the UserService

    @Mock
    private Auth0Service auth0Service;  // Mock the Auth0Service

    private MockMvc mockMvc;  // For performing mock HTTP requests

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initializes the mock annotations
        mockMvc = MockMvcBuilders.standaloneSetup(userRestController).build();  // Set up MockMvc
    }

    @Test
    public void testCreateUser() throws Exception {
        // Arrange
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setEmail("testuser@example.com");
        userRequest.setName("Test User");
        userRequest.setPhoneNumber("1234567890");

        // Mocking the service method calls
        when(userService.isUserPresent(any(String.class))).thenReturn(false);

        // Act
        mockMvc.perform(post("/api/create-user")
                        .contentType("application/json")
                        .content("{\"email\":\"testuser@example.com\",\"name\":\"Test User\",\"phoneNumber\":\"1234567890\"}"))
                .andExpect(status().isOk());

        // Assert
        verify(auth0Service, times(1)).createUserInAuth0(userRequest.getEmail());  // Verify Auth0 service
        verify(auth0Service, times(1)).triggerPasswordReset(userRequest.getEmail());  // Verify password reset is called
    }
    // Controller method to create the user
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
            auth0Service.triggerPasswordReset(request.getEmail());  // Ensure this is being invoked

            // Return success response
            return ResponseEntity.ok("✅ User created: " + request.getEmail());
        } catch (Exception e) {
            // Handle exceptions and return an error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Failed to create user: " + e.getMessage());
        }
    }
}