// src/main/java/com/java/firstspringproject/config/SecurityConfig.java
package com.java.firstspringproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/profile").authenticated()
                        .requestMatchers("/api/create-user").permitAll()  // ✅ allow this
                        .anyRequest().permitAll()
                )

                .oauth2ResourceServer(oauth2 -> oauth2.jwt())  // ✅ enable JWT token checking
                .oauth2Login(Customizer.withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
//                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")) // disable CSRF for APIs
                .logout(logout -> logout.logoutSuccessHandler(oidcLogoutSuccessHandler(null)));
        return http.build();
    }


    @Bean
    public LogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository repo) {
        OidcClientInitiatedLogoutSuccessHandler successHandler =
                new OidcClientInitiatedLogoutSuccessHandler(repo);

        successHandler.setPostLogoutRedirectUri("http://localhost:8080/login");
        return successHandler;
    }
}
