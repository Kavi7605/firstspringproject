package com.java.firstspringproject.controller;

import com.java.firstspringproject.model.User;
import com.java.firstspringproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

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

            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                model.addAttribute("phoneNumber", user.getPhoneNumber());
                model.addAttribute("loginStatus", "✅ User found in local DB. Login successful.");
            } else {
                model.addAttribute("loginStatus", "❌ User is authenticated with Auth0, but not found in local DB.");
            }
        } else {
            model.addAttribute("loginStatus", "You are not logged in.");
        }
        return "index";
    }
}
