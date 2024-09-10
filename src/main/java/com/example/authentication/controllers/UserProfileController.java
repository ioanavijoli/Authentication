package com.example.authentication.controllers;

import com.example.authentication.config.JwtUtils;
import com.example.authentication.entity.User;
import com.example.authentication.services.UserService;
import com.example.authentication.dto.UserProfileDTO;
import com.example.authentication.entity.UserProfile;
import com.example.authentication.services.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.Map;
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
    @GetMapping
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtConfig.extractUsername(token);
        User user = userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        UserProfile userProfile = userProfileService.findByUserUUID(user.getUUID());

        if (userProfile == null) {
            return new ResponseEntity<>("Profile not found", HttpStatus.NOT_FOUND);
        }

        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setFirstname(userProfile.getFirstname());
        userProfileDTO.setSurname(userProfile.getSurname());
        userProfileDTO.setBirthday(userProfile.getBirthday());
        userProfileDTO.setReligion(userProfile.getReligion());
        userProfileDTO.setGender(userProfile.getGender());
        userProfileDTO.setRelationship(userProfile.getRelationship());

        return ResponseEntity.ok(userProfileDTO);
    }

    @PostMapping
    public ResponseEntity<?> saveProfile(@RequestBody UserProfileDTO userProfileDTO, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtConfig.extractUsername(token);
        User user = userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        UserProfile existingProfile = userProfileService.findByUserUUID(user.getUUID());

        if (existingProfile == null) {
            try {
                userProfileService.createProfile(userProfileDTO, user);
                return new ResponseEntity<>(filterNonNullFields(userProfileDTO), HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
            userProfileService.updateProfile(userProfileDTO, existingProfile);
            return new ResponseEntity<>(filterNonNullFields(userProfileDTO), HttpStatus.OK);
        }
    }

    private Map<String, Object> filterNonNullFields(UserProfileDTO userProfileDTO) {
        Map<String, Object> nonNullFields = new HashMap<>();

        if (userProfileDTO.getFirstname() != null) {
            nonNullFields.put("firstname", userProfileDTO.getFirstname());
        }
        if (userProfileDTO.getSurname() != null) {
            nonNullFields.put("surname", userProfileDTO.getSurname());
        }
        if (userProfileDTO.getBirthday() != null) {
            nonNullFields.put("birthday", userProfileDTO.getBirthday());
        }
        if (userProfileDTO.getReligion() != null) {
            nonNullFields.put("religion", userProfileDTO.getReligion());
        }
        if (userProfileDTO.getGender() != null) {
            nonNullFields.put("gender", userProfileDTO.getGender());
        }
        if (userProfileDTO.getRelationship() != null) {
            nonNullFields.put("relationship", userProfileDTO.getRelationship());
        }

        return nonNullFields;
    }

}
