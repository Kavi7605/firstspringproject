// src/main/java/com/java/firstspringproject/controller/WebUiController.java
package com.java.firstspringproject.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class WebUiController {

    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser != null) {
            model.addAttribute("email", oidcUser.getEmail());
            model.addAttribute("name", oidcUser.getFullName());
        }
        model.addAttribute("logs", List.of("Welcome to the UI", "Use the form below to test."));
        return "index";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        model.addAttribute("email", oidcUser.getEmail());
        model.addAttribute("name", oidcUser.getFullName());
        return "profile";
    }
}
