package com.example.authentication.controllers;

import com.example.authentication.config.JwtUtils;
import com.example.authentication.dto.AuthenticationRequestDTO;
import com.example.authentication.dto.UserDTO;
import com.example.authentication.entity.User;
import com.example.authentication.entity.UserRole;
import com.example.authentication.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticate(@RequestBody AuthenticationRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            User user = userService.findByUsername(request.getUsername()).orElse(null);

            if (user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("accessToken", jwtUtils.generateToken(user.getUsername(), user.getRole()));
                response.put("username", user.getUsername());
                response.put("email", user.getEmail());
                response.put("role", user.getRole().toString());
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestBody UserDTO request) {
        if (userService.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        if (request.getUsername().isEmpty() || request.getEmail().isEmpty() || request.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        UserRole role = (request.getRole() != null) ? request.getRole() : UserRole.USER;

        User newUser = new User(request.getUsername(), request.getEmail(), request.getPassword(), role);
        userService.createUser(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
}
