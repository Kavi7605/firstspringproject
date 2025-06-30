package com.java.firstspringproject.model;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateUserRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String name;

    @NotBlank
    @Pattern(regexp = "\\+\\d{1,3}", message = "Country code must start with + and be 1–3 digits")
    private String countryCode;

    @Size(min = 10, max = 10, message = "Phone number must be exactly 10 digits")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String localPhoneNumber;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getLocalPhoneNumber() {
        return localPhoneNumber;
    }

    public void setLocalPhoneNumber(String localPhoneNumber) {
        this.localPhoneNumber = localPhoneNumber;
    }

    // This builds the full E.164 phone number
    public String getPhoneNumber() {
        return countryCode + localPhoneNumber;
    }
}