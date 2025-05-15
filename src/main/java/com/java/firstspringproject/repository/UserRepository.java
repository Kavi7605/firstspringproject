// src/main/java/com/java/firstspringproject/repository/UserRepository.java
package com.java.firstspringproject.repository;

import com.java.firstspringproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
