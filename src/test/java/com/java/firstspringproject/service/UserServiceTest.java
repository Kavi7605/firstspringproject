package com.java.firstspringproject.service;

import com.java.firstspringproject.model.CreateUserRequest;
import com.java.firstspringproject.model.User;
import com.java.firstspringproject.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserService userService;
    private Auth0Service auth0Service;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        auth0Service = mock(Auth0Service.class);
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository, auth0Service);
    }

    @Test
    public void testRegisterWithoutPassword_CreatesAuth0UserAndSavesLocally() {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("test@example.com");
        req.setName("Test User");
        req.setCountryCode("+91");
        req.setLocalPhoneNumber("1234567890");

        when(auth0Service.createUserWithoutPassword(req)).thenReturn("auth0|abc123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.registerWithoutPassword(req);

        verify(auth0Service).createUserWithoutPassword(req);
        verify(auth0Service).sendPasswordResetEmail("auth0|abc123");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();
        assertEquals("test@example.com", saved.getEmail());
        assertEquals("1234567890", saved.getPhoneNumber());
        assertEquals("Test User", saved.getName());
        assertEquals("auth0|abc123", saved.getAuth0Id());
    }
}
