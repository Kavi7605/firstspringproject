// src/main/java/com/java/firstspringproject/service/UserService.java
package com.java.firstspringproject.service;

import com.java.firstspringproject.model.CreateUserRequest;
import com.java.firstspringproject.model.User;
import com.java.firstspringproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Auth0Service auth0Service;

    public boolean isUserPresent(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void createUser(CreateUserRequest request) {
        try {
            // Debug log for phone number
            System.out.println("Phone from request: " + request.getPhoneNumber());

            // First create in Auth0
            auth0Service.createUserInAuth0(
                request.getEmail(),
                request.getName(),
                request.getPassword(),
                request.getPhoneNumber()
            );

            // Only if Auth0 creation succeeds, save to local DB
            User user = new User();
            user.setEmail(request.getEmail());
            user.setName(request.getName());
            user.setPhoneNumber(request.getPhoneNumber());
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }
}
