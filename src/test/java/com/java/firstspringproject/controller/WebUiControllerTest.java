package com.java.firstspringproject.controller;

import com.java.firstspringproject.model.User;
import com.java.firstspringproject.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebUiControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private Model model;
    @Mock
    private OidcUser oidcUser;

    @InjectMocks
    private WebUiController webUiController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void index_WhenUserAuthenticatedAndFoundInDb_ShouldSetAttributesAndReturnIndex() {
        String email = "test@example.com";
        String name = "Test User";
        String phone = "1234567890";
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPhoneNumber(phone);

        when(oidcUser.getEmail()).thenReturn(email);
        when(oidcUser.getFullName()).thenReturn(name);
        when(userService.findByEmail(email)).thenReturn(Optional.of(user));

        String view = webUiController.index(model, oidcUser);

        verify(model).addAttribute("email", email);
        verify(model).addAttribute("name", name);
        verify(model).addAttribute("phoneNumber", phone);
        verify(model).addAttribute("loginStatus", "✅ User found in local DB. Login successful.");
        assertEquals("index", view);
    }

    @Test
    void index_WhenUserAuthenticatedButNotFoundInDb_ShouldSetNotFoundStatus() {
        String email = "notfound@example.com";
        String name = "Not Found";
        when(oidcUser.getEmail()).thenReturn(email);
        when(oidcUser.getFullName()).thenReturn(name);
        when(userService.findByEmail(email)).thenReturn(Optional.empty());

        String view = webUiController.index(model, oidcUser);

        verify(model).addAttribute("email", email);
        verify(model).addAttribute("name", name);
        verify(model, never()).addAttribute(eq("phoneNumber"), any());
        verify(model).addAttribute("loginStatus", "❌ User is authenticated with Auth0, but not found in local DB.");
        assertEquals("index", view);
    }

    @Test
    void index_WhenUserNotAuthenticated_ShouldSetNotLoggedInStatus() {
        String view = webUiController.index(model, null);
        verify(model).addAttribute("loginStatus", "You are not logged in.");
        assertEquals("index", view);
    }
} 