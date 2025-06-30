package com.java.firstspringproject.controller;

import com.java.firstspringproject.model.CreateUserRequest;
import com.java.firstspringproject.model.User;
import com.java.firstspringproject.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated @RequestBody CreateUserRequest req) {
        User user = userService.registerWithoutPassword(req);
        return ResponseEntity.ok(Map.of(
                "message", "User registered. Password reset email sent.",
                "email", user.getEmail()
        ));
    }
}