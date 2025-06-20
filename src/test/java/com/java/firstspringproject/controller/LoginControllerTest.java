package com.java.firstspringproject.controller;

import com.java.firstspringproject.config.Auth0Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.view.RedirectView;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginControllerTest {

    @Mock
    private Auth0Properties auth0Properties;
    private LoginController loginController;
    private final String redirectUri = "http://localhost:8080/login/oauth2/code/auth0";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(auth0Properties.getDomain()).thenReturn("dev-r7dmklkhsqoapi7f.us.auth0.com");
        when(auth0Properties.getClientId()).thenReturn("ywlHg6XqhtVnOKjE0lYHzP607yOqgvuB");
        loginController = new LoginController(auth0Properties, redirectUri);
    }

    @Test
    void loginPage_ShouldReturnLoginView() {
        String viewName = loginController.loginPage();
        assertEquals("login", viewName, "loginPage() should return 'login' view name");
    }

    @Test
    void loginAfterFullLogout_ShouldReturnCorrectRedirectView() {
        RedirectView redirectView = loginController.loginAfterFullLogout();
        assertNotNull(redirectView, "RedirectView should not be null");
        String url = redirectView.getUrl();
        assertNotNull(url, "Redirect URL should not be null");
        assertTrue(url.startsWith("https://dev-r7dmklkhsqoapi7f.us.auth0.com/v2/logout"), "URL should start with Auth0 logout endpoint");
        assertTrue(url.contains("client_id=ywlHg6XqhtVnOKjE0lYHzP607yOqgvuB"), "URL should contain client_id");
        assertTrue(url.contains("returnTo=https://dev-r7dmklkhsqoapi7f.us.auth0.com/authorize"), "URL should contain returnTo param");
        assertTrue(url.contains("redirect_uri=http://localhost:8080/login/oauth2/code/auth0"), "URL should contain redirect_uri");
        assertTrue(url.contains("scope=openid%20profile%20email"), "URL should contain scope");
        assertTrue(url.contains("prompt=login"), "URL should contain prompt=login");
    }
} 