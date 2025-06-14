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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

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
        createUserRequest.setPassword("password123");
    }

    @Test
    void createUser_ShouldSaveUserToDbAndAuth0() {
        // When
        userService.createUser(createUserRequest);

        // Then
        verify(userRepository).save(any(User.class));
        verify(auth0Service).createUserInAuth0(createUserRequest.getEmail(), createUserRequest.getPassword());
    }
}