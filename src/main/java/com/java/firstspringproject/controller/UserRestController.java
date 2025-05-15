// src/main/java/com/java/firstspringproject/controller/UserRestController.java
package com.java.firstspringproject.controller;

import org.springframework.security.oauth2.jwt.Jwt;
import com.java.firstspringproject.model.CreateUserRequest;
import com.java.firstspringproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
//import com.java.firstspringproject.service.LogService;

@RestController
@RequestMapping("/api")
public class UserRestController {

    @Autowired
    private UserService userService;

//    @Autowired
//    private LogService logService;

    @GetMapping("/check-user")
    public ResponseEntity<String> checkUser(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        String sub = jwt.getSubject();
        String issuer = jwt.getIssuer().toString();

//        logService.add("📥 Received JWT token");
//        logService.add("🔓 Decoded JWT:");
//        logService.add("  • subject: " + sub);
//        logService.add("  • issuer: " + issuer);
//        logService.add("  • email claim: " + email);

        if (userService.isUserPresent(email)) {
//            logService.add("✅ Local DB match found for: " + email);
//            logService.add("✅ User successfully authenticated and authorized.");
            return ResponseEntity.ok("✅ Authorized user: " + email);
        } else {
//            logService.add("❌ No user found in local DB for: " + email);
//            logService.add("❌ Access denied.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ User not found in DB: " + email);
        }
    }
}
