package com.java.firstspringproject.controller;

import com.java.firstspringproject.config.Auth0Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class LoginController {

    private final Auth0Properties auth0Properties;
    private final String redirectUri;

    public LoginController(Auth0Properties auth0Properties, @Value("${auth0.mgmt.redirect-uri:http://localhost:8080/login/oauth2/code/auth0}") String redirectUri) {
        this.auth0Properties = auth0Properties;
        this.redirectUri = redirectUri;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // your login.html with the login button
    }

    @GetMapping("/auth0-login")
    public RedirectView loginAfterFullLogout() {
        String auth0Domain = auth0Properties.getDomain();
        String clientId = auth0Properties.getClientId();

        String logoutThenLogin = "https://" + auth0Domain + "/v2/logout" +
                "?client_id=" + clientId +
                "&returnTo=https://" + auth0Domain + "/authorize" +
                "?response_type=code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&scope=openid%20profile%20email" +
                "&prompt=login";

        return new RedirectView(logoutThenLogin);
    }
}
