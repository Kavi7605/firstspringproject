package com.java.firstspringproject.controller;

import com.java.firstspringproject.model.User;
import com.java.firstspringproject.repository.UserRepository;
import com.java.firstspringproject.service.Auth0Service;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/ui")
public class WebUiController {

    private final Auth0Service auth0Service;
    private final UserRepository userRepository;
    private final List<String> logs = new ArrayList<>();

    public WebUiController(Auth0Service auth0Service, UserRepository userRepository) {
        this.auth0Service = auth0Service;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/ui";
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("logs", logs);
        return "index";
    }

    @PostMapping("/create-user")
    public String createUser(Model model) {
        logs.clear();
        logs.add("🔵 Creating user...");

        try {
            String email = "test" + System.currentTimeMillis() + "@example.com";
            String name = "Test User";
            String password = "TestPass123!";

            logs.add("→ Creating in Auth0...");
            String auth0Id = auth0Service.createAuth0User(email, name, password);
            logs.add("✅ Created in Auth0 with ID: " + auth0Id);

            User user = new User(auth0Id, email, name);
            userRepository.save(user);
            logs.add("✅ Saved to local PostgreSQL DB");

        } catch (Exception e) {
            logs.add("❌ Failed: " + e.getMessage());
        }

        model.addAttribute("logs", logs);
        return "index";
    }

    @GetMapping("/me")
    public String me(@AuthenticationPrincipal Jwt jwt, Model model) {
        logs.clear();

        if (jwt == null) {
            logs.add("❌ No token found. Please login first.");
            model.addAttribute("logs", logs);
            return "index";
        }

        String userId = jwt.getSubject();
        logs.add("🔐 Received JWT, user_id: " + userId);

        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            logs.add("✅ User found in PostgreSQL DB.");
            logs.add("👤 Email: " + user.get().getEmail());
        } else {
            logs.add("❌ User NOT found in local DB.");
        }

        model.addAttribute("logs", logs);
        return "index";
    }
}
