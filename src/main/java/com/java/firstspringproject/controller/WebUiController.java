package com.java.firstspringproject.controller;

import com.java.firstspringproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebUiController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser != null) {
            String email = oidcUser.getEmail();
            model.addAttribute("email", email);
            model.addAttribute("name", oidcUser.getFullName());

            boolean present = userService.isUserPresent(email);
            model.addAttribute("loginStatus", present
                    ? "✅ User found in local DB. Login successful."
                    : "❌ User is authenticated with Auth0, but not found in local DB.");
        } else {
            model.addAttribute("loginStatus", "You are not logged in.");
        }
        return "index";
    }
}
