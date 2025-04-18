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
    public String email; 
    public String contact; 
    public User() {
    }

    
    // Constructors, getters, and setters
    public User(String username, String password, String email,String contact) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.contact = contact;
    }

    // Getters and Setters
}
