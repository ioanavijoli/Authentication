package com.example.authentication.controllers;

import com.example.authentication.config.JwtUtils;
import com.example.authentication.entity.User;
import com.example.authentication.services.UserService;
import com.example.authentication.dto.UserProfileDTO;
import com.example.authentication.entity.UserProfile;
import com.example.authentication.services.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
public class UserProfileController {

    private final UserService userService;
    private final UserProfileService userProfileService;
    private final JwtUtils jwtConfig;

    @Autowired
    public UserProfileController(UserService userService, UserProfileService userProfileService, JwtUtils jwtConfig) {
        this.userService = userService;
        this.userProfileService = userProfileService;
        this.jwtConfig = jwtConfig;
    }

    @PostMapping("/createProfile")
    public ResponseEntity<?> createProfile(@RequestBody UserProfileDTO userProfile, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtConfig.extractUsername(token);
        User user = userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            userProfileService.createProfile(userProfile, user);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Your profile has been created", HttpStatus.CREATED);
    }

    @PutMapping("/changeProfile")

    public ResponseEntity<String> changeProfile(@RequestBody UserProfileDTO newProfile, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtConfig.extractUsername(token);
        User user = userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        UserProfile userProfile = userProfileService.findByUserUUID(user.getUUID());

        userProfileService.updateProfile(newProfile, userProfile);

        return ResponseEntity.ok("Profile changed");
    }
}
