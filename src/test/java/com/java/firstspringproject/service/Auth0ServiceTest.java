package com.java.firstspringproject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class Auth0ServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private Auth0Service auth0Service;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(webClientBuilder.build()).thenReturn(webClient);
        auth0Service = new Auth0Service(webClientBuilder);
        injectPrivate("clientId", "dummy-client-id");
        injectPrivate("clientSecret", "dummy-secret");
        injectPrivate("audience", "dummy-audience");
        injectPrivate("domain", "dummy-domain.auth0.com");
    }

    private void injectPrivate(String fieldName, String value) throws Exception {
        Field field = Auth0Service.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(auth0Service, value);
    }

    @Test
    void testCreateUserInAuth0_Success() {
        String email = "test@example.com";
        String password = "Pass@123";
        String token = "mock_token";

        // --- Token request chain ---
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("access_token", token);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(contains("oauth/token"))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any(Map.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(Map.class))).thenReturn(Mono.just(tokenMap));

        // --- User creation chain ---
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(contains("api/v2/users"))).thenReturn(requestBodyUriSpec);
        // Mock headers(...) to return the same spec for chaining
        when(requestBodyUriSpec.headers(any())).then(invocation -> {
            // Call the headers consumer with a dummy HttpHeaders object if needed
            return requestBodyUriSpec;
        });
        when(requestBodyUriSpec.bodyValue(any(Map.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(String.class))).thenReturn(Mono.just("{\"user_id\":\"auth0|123\"}"));

        // Should not throw
        assertDoesNotThrow(() -> auth0Service.createUserInAuth0(email, password));
    }

    @Test
    void testCreateUserInAuth0_TokenFailure() {
        String email = "fail@example.com";
        String password = "Pass@123";

        // Token request chain fails
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(contains("oauth/token"))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any(Map.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(Map.class))).thenReturn(Mono.error(new RuntimeException("token failure")));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> auth0Service.createUserInAuth0(email, password));
        assertTrue(ex.getMessage().contains("token failure"));
    }

    @Test
    void testCreateUserInAuth0_UserCreationFailure() {
        String email = "failuser@example.com";
        String password = "Pass@123";
        String token = "mock_token";

        // --- Token request chain ---
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("access_token", token);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(contains("oauth/token"))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any(Map.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(Map.class))).thenReturn(Mono.just(tokenMap));

        // --- User creation chain fails ---
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(contains("api/v2/users"))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.headers(any())).then(invocation -> requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any(Map.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(String.class))).thenReturn(Mono.error(new RuntimeException("user creation failure")));

        // Should not throw, as the error is handled in doOnError and the method is void
        assertDoesNotThrow(() -> auth0Service.createUserInAuth0(email, password));
    }
}
