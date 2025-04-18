package com.school.vaccination.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.school.vaccination.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        return ResponseEntity.ok(authService.logIn(username, password));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        return ResponseEntity.ok(authService.signUp(username, password));
    }
}

