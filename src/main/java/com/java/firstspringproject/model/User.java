// src/main/java/com/java/firstspringproject/model/User.java
package com.java.firstspringproject.model;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user")  // ✅ match your DB table
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ✅ auto-generate the ID
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
