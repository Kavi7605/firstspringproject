package com.java.firstspringproject.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "auth0.mgmt")
public class Auth0Properties {
    private String clientId;
    private String clientSecret;
    private String audience;
    private String domain;
}
