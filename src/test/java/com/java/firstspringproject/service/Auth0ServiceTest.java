package com.java.firstspringproject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;

import java.lang.reflect.Field;

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
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

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
        injectPrivate("audience", "https://dummy-api/");
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

        // Token request
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(contains("oauth/token"))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(BodyInserter.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(String.class)))
                .thenReturn(Mono.just("{\"access_token\":\"mock_token\"}"));

        // User creation
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(contains("api/v2/users"))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.header(eq(HttpHeaders.AUTHORIZATION), anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(BodyInserter.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        assertDoesNotThrow(() -> auth0Service.createUserInAuth0(email, password));
    }

    @Test
    void testCreateUserInAuth0_TokenFailure() {
        String email = "fail@example.com";
        String password = "Pass@123";

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(contains("oauth/token"))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(BodyInserter.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(String.class)))
                .thenReturn(Mono.error(new RuntimeException("token failure")));

        Exception ex = assertThrows(RuntimeException.class,
                () -> auth0Service.createUserInAuth0(email, password));

        assertTrue(ex.getMessage().contains("token failure"));
    }
}
