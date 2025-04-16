package com.school.vaccination.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "users")
@Data
public class User {

    @Id
    private String id;
    private String username;
    public String password;  // Hashed password

    // Constructors, getters, and setters
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
}
