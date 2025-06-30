// src/main/java/com/java/firstspringproject/model/User.java
package com.java.firstspringproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.Objects;

@Entity
@Table(name = "app_user")  // ✅ match your DB table
public class User {

    @Column(name = "auth0_id")
    private String auth0Id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ✅ auto-generate the ID
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @NotBlank
    @Pattern(regexp = "^\\+\\d{1,3}\\d{10}$", message = "Phone number must start with country code and contain 10 digits")
    private String phoneNumber;


    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAuth0Id() {
        return auth0Id;
    }
    public void setAuth0Id(String auth0Id) {
        this.auth0Id = auth0Id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
               Objects.equals(email, user.email) &&
               Objects.equals(name, user.name) &&
               Objects.equals(phoneNumber, user.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, name, phoneNumber);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
