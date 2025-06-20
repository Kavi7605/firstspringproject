package com.java.firstspringproject.controller;

import com.java.firstspringproject.model.CreateUserRequest;
import com.java.firstspringproject.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRestControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserRestController userRestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Success() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(userService.isUserPresent(request.getEmail())).thenReturn(false);
        doNothing().when(userService).createUser(request);

        ResponseEntity<String> response = userRestController.createUser(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("✅ User created"));
    }

    @Test
    void testCreateUser_UserAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password");

        when(userService.isUserPresent(request.getEmail())).thenReturn(true);

        ResponseEntity<String> response = userRestController.createUser(request);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().contains("⚠️ User already exists"));
    }

    @Test
    void testCreateUser_Failure() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("fail@example.com");
        request.setPassword("password");

        when(userService.isUserPresent(request.getEmail())).thenReturn(false);
        doThrow(new RuntimeException("DB error")).when(userService).createUser(request);

        ResponseEntity<String> response = userRestController.createUser(request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("❌ Failed to create user"));
        assertTrue(response.getBody().contains("DB error"));
    }
} 