package com.java.firstspringproject.controller;

import com.java.firstspringproject.model.CreateUserRequest;
import com.java.firstspringproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api") // ✅ required to prefix all endpoints with /api
public class UserRestController {

    @Autowired
    private UserService userService;

    @PostMapping("/create-user")
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest request) {
        if (userService.isUserPresent(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("⚠️ User already exists in DB.");
        }

        try {
            userService.createUser(request);
            return ResponseEntity.ok("✅ User created: " + request.getEmail());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Failed to create user: " + e.getMessage());
        }
    }
}
