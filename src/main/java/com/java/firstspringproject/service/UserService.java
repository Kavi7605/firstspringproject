package com.java.firstspringproject.service;

import com.java.firstspringproject.model.CreateUserRequest;
import com.java.firstspringproject.model.User;
import com.java.firstspringproject.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final Auth0Service auth0Service;

    public UserService(UserRepository userRepository, Auth0Service auth0Service) {
        this.userRepository = userRepository;
        this.auth0Service = auth0Service;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Transactional
    public User registerWithoutPassword(CreateUserRequest req) {
        String auth0UserId = auth0Service.createUserWithoutPassword(req);
//        auth0Service.sendPasswordResetEmail(auth0UserId);

        User user = new User();
        user.setAuth0Id(auth0UserId);
        user.setEmail(req.getEmail());
        user.setName(req.getName());
        user.setPhoneNumber(req.getPhoneNumber());

        return userRepository.save(user);
    }
}
