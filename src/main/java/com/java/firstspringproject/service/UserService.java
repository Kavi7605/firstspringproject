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

    public void createUser(CreateUserRequest request) {
        // Save to local DB
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        userRepository.save(user);

        // Save to Auth0
        auth0Service.createUserInAuth0(request.getEmail(), request.getPassword());
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
