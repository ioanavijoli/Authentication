package com.example.authentication.controllers;

import com.example.authentication.config.JwtUtils;
import com.example.authentication.entity.User;
import com.example.authentication.mapper.UserProfileMapper;
import com.example.authentication.services.UserService;
import com.example.authentication.dto.UserProfileDTO;
import com.example.authentication.entity.UserProfile;
import com.example.authentication.services.UserProfileService;
import com.example.authentication.util.ImageUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        userProfileDTO.setGender(userProfile.getGender());
        userProfileDTO.setSmallAvatar(userProfile.getSmallAvatar());
        return ResponseEntity.ok(userProfileDTO);
    }

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtConfig.extractUsername(token);
        User user = userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        try {
            byte[] compressedImage = ImageUtil.compressImage(file.getBytes());

            UserProfile existingProfile = userProfileService.findByUserUUID(user.getUUID());
            UserProfileDTO userProfileDTO;

            if (existingProfile == null) {
                userProfileDTO = new UserProfileDTO();
                userProfileDTO.setSmallAvatar(compressedImage);

                userProfileService.createProfile(userProfileDTO, user);

                UserProfile createdProfile = userProfileService.findByUserUUID(user.getUUID());
                userProfileDTO = UserProfileMapper.convertToDTO(createdProfile);

                return new ResponseEntity<>(userProfileDTO, HttpStatus.CREATED);
            }

            userProfileDTO = new UserProfileDTO();
            userProfileDTO.setSmallAvatar(compressedImage);

            userProfileService.updateProfile(userProfileDTO, existingProfile);

            UserProfile updatedProfile = userProfileService.findByUserUUID(user.getUUID());
            userProfileDTO = UserProfileMapper.convertToDTO(updatedProfile);

            return new ResponseEntity<>(userProfileDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
        if (userProfileDTO.getSmallAvatar() != null) {
            nonNullFields.put("avatar", userProfileDTO.getSmallAvatar());
        }
        if (userProfileDTO.getGender() != null) {
            nonNullFields.put("gender", userProfileDTO.getGender());
        }
        return nonNullFields;
    }

    @GetMapping("/avatar")
    public ResponseEntity<byte[]> getImage(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtConfig.extractUsername(token);
        User user = userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        UserProfileDTO userProfileDTO = userProfileService.getUserProfileDTO(user.getUUID());
        if (userProfileDTO == null || userProfileDTO.getSmallAvatar() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/jpeg"))
                    .body(userProfileDTO.getSmallAvatar());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
