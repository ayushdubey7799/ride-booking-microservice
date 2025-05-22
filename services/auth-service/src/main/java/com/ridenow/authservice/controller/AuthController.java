package com.ridenow.authservice.controller;

import com.ridenow.authservice.domain.UserAuthEntity;
import com.ridenow.authservice.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserAuthService userAuthService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserAuthEntity userAuthEntity) {
        userAuthService.save(userAuthEntity);
        return ResponseEntity.ok("Registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> req) {

        return ResponseEntity.ok("logged in successfully");
    }
}