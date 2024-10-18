package com.example.authentication.controllers;

import com.example.authentication.config.JwtUtils;
import com.example.authentication.dto.UserProfileDTO;
import com.example.authentication.entity.User;
import com.example.authentication.entity.UserProfile;
import com.example.authentication.mapper.UserProfileMapper;
import com.example.authentication.services.UserProfileService;
import com.example.authentication.services.UserService;
import com.example.authentication.util.ImageUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

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

        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errorCode", HttpStatus.NOT_FOUND.value(),
                            "errorKey", "USER_NOT_FOUND",
                            "errorMsg", "User not found"));
        }

        UserProfile userProfile = userProfileService.findByUserUUID(user.getUUID());
        if (userProfile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errorCode", HttpStatus.NOT_FOUND.value(),
                            "errorKey", "PROFILE_NOT_FOUND",
                            "errorMsg", "Profile not found"));
        }

        UserProfileDTO userProfileDTO = UserProfileMapper.convertToDTO(userProfile);
        return ResponseEntity.ok(userProfileDTO);
    }


    @PostMapping("/avatar")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtConfig.extractUsername(token);

        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errorCode", HttpStatus.NOT_FOUND.value(),
                            "errorKey", "USER_NOT_FOUND",
                            "errorMsg", "User not found"));
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "errorKey", "IMAGE_UPLOAD_FAILED",
                            "errorMsg", e.getMessage()));
        }
    }


    @PostMapping
    public ResponseEntity<?> saveProfile(@RequestBody UserProfileDTO userProfileDTO, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtConfig.extractUsername(token);

        User user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errorCode", HttpStatus.NOT_FOUND.value(),
                            "errorKey", "USER_NOT_FOUND",
                            "errorMsg", "User not found"));
        }

        UserProfile existingProfile = userProfileService.findByUserUUID(user.getUUID());
        if (existingProfile == null) {
            try {
                userProfileService.createProfile(userProfileDTO, user);
                return new ResponseEntity<>(filterNonNullFields(userProfileDTO), HttpStatus.CREATED);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("errorCode", HttpStatus.BAD_REQUEST.value(),
                                "errorKey", "PROFILE_CREATION_FAILED",
                                "errorMsg", e.getMessage()));
            }
        } else {
            userProfileService.updateProfile(userProfileDTO, existingProfile);
            return ResponseEntity.ok(filterNonNullFields(userProfileDTO));
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
        if (userProfileDTO.getAddress() != null) {
            nonNullFields.put("address", userProfileDTO.getAddress());

        }
        return nonNullFields;
    }

    @GetMapping("/avatar")
    public ResponseEntity<?> getImage(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtConfig.extractUsername(token);
        User user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errorCode", HttpStatus.NOT_FOUND.value(),
                            "errorKey", "USER_NOT_FOUND",
                            "errorMsg", "User not found"));
        }
        UserProfileDTO userProfileDTO = userProfileService.getUserProfileDTO(user.getUUID());
        if (userProfileDTO == null || userProfileDTO.getSmallAvatar() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errorCode", HttpStatus.NOT_FOUND.value(),
                            "errorKey", "AVATAR_NOT_FOUND",
                            "errorMsg", "Avatar not found"));
        }

        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/jpeg"))
                    .body(userProfileDTO.getSmallAvatar());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "errorKey", "IMAGE_FETCH_FAILED",
                            "errorMsg", e.getMessage()));
        }
    }

}
