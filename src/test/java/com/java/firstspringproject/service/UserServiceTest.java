package com.java.firstspringproject.service;

import com.java.firstspringproject.model.CreateUserRequest;
import com.java.firstspringproject.model.User;
import com.java.firstspringproject.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Auth0Service auth0Service;

    @InjectMocks
    private UserService userService;

    @Test
//    @MockitoSettings(strictness = Strictness.LENIENT)
    void createUser_success() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@example.com");
        request.setName("Test User");
        request.setPassword("SecurePassword123");
        request.setPhoneNumber("9876543210");

//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act
        userService.createUser(request);

        // Assert
        verify(auth0Service, times(1)).createUserInAuth0(
                eq("test@example.com"),
                eq("SecurePassword123")
        );

        verify(userRepository, times(1)).save(any(User.class));
    }
}