package com.school.vaccination.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.school.vaccination.model.User;
import com.school.vaccination.repositories.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;


    // Sign-Up (Register)
    public String signUp(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        User user = new User(username, password);
        userRepository.save(user);
        return "User registered successfully";
    }

    // Log-In (Authenticate)
    public String logIn(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        return "Authentication successful";  // You will issue a JWT token here in a real-world case
    }
}
