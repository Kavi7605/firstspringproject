package com.java.firstspringproject.service;

import com.java.firstspringproject.model.User;
import com.java.firstspringproject.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void processOAuth2User(Map<String, Object> attributes) {
        logger.info("Auth0 attributes: {}", attributes);  // Add this line for debugging

        String auth0UserId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        if (auth0UserId == null || email == null) {
            logger.warn("Missing user information: sub={}, email={}", auth0UserId, email);
            return;
        }

        userRepository.findById(auth0UserId)
                .ifPresentOrElse(
                        user -> {
                            logger.info("User with ID {} already exists in the database.", auth0UserId);
                            // You might want to update user details here if necessary
                        },
                        () -> {
                            if (!userRepository.existsByEmail(email)) {
                                User newUser = new User(auth0UserId, email, name);
                                userRepository.save(newUser);
                                logger.info("New user with ID {} and email {} saved to the database.", auth0UserId, email);
                            } else {
                                logger.warn("User with email {} already exists, but different Auth0 ID ({}). Consider linking accounts.", email, auth0UserId);
                                // Handle the case where the email exists but the Auth0 ID is different
                                // This might require a different user association strategy
                            }
                        }
                );
    }

    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    // Add other user-related business logic here
}