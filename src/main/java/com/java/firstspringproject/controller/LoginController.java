package com.java.firstspringproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // your login.html with the login button
    }

    @GetMapping("/auth0-login")
    public RedirectView loginAfterFullLogout() {
        String auth0Domain = "dev-r7dmklkhsqoapi7f.us.auth0.com";
        String clientId = "ywlHg6XqhtVnOKjE0lYHzP607yOqgvuB";
        String redirectUri = "http://localhost:8080/login/oauth2/code/auth0";

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
