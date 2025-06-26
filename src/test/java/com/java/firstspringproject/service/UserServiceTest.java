package com.java.firstspringproject.service;

import com.java.firstspringproject.model.CreateUserRequest;
import com.java.firstspringproject.model.User;
import com.java.firstspringproject.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Auth0Service auth0Service;

    @InjectMocks
    private UserService userService;

    private CreateUserRequest createUserRequest;

    @BeforeEach
    void setUp() {
        createUserRequest = new CreateUserRequest();
        createUserRequest.setEmail("test@example.com");
        createUserRequest.setName("Test User");
    }

    @Test
    void createUser_ShouldSaveUserToDbAndAuth0() {
        // When
        userService.createUser(createUserRequest);

        // Then
        verify(userRepository).save(any(User.class));
        verify(auth0Service).createUserInAuth0(createUserRequest.getEmail());
    }

    @Test
    void isUserPresent_WhenUserExists_ShouldReturnTrue() {
        // Given
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setName("Test User");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        boolean result = userService.isUserPresent(email);

        // Then
        assertTrue(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void isUserPresent_WhenUserDoesNotExist_ShouldReturnFalse() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        boolean result = userService.isUserPresent(email);

        // Then
        assertFalse(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void findByEmail_WhenUserExists_ShouldReturnUser() {
        // Given
        String email = "test@example.com";
        User expectedUser = new User();
        expectedUser.setEmail(email);
        expectedUser.setName("Test User");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));

        // When
        Optional<User> result = userService.findByEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        assertEquals("Test User", result.get().getName());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void findByEmail_WhenUserDoesNotExist_ShouldReturnEmptyOptional() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findByEmail(email);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findByEmail(email);
    }
}