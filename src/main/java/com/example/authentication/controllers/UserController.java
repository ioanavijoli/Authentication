package com.example.authentication.controllers;

import com.example.authentication.config.JwtUtils;
import com.example.authentication.entity.Info;
import com.example.authentication.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    private final JwtUtils jwtConfig;

    @Autowired
    public UserController(UserService userService, JwtUtils jwtConfig) {
        this.userService = userService;
        this.jwtConfig = jwtConfig;
    }

    @PostMapping("/favorites/{infoId}")
    public ResponseEntity<?> addFavorite(@PathVariable String infoId, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtConfig.extractUsername(token);

        try {
            userService.addToFavorites(username, infoId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Carehome added to favorites");
            response.put("infoId", infoId);

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("infoId", infoId);

            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }


    @DeleteMapping("/favorites/{infoId}")
    public ResponseEntity<?> removeFavorite(@PathVariable String infoId, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtConfig.extractUsername(token);

        try {
            userService.removeFromFavorites(username, infoId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Carehome removed from favorites");
            response.put("infoId", infoId);

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("infoId", infoId);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @GetMapping("/favorites")
    public ResponseEntity<List<Info>> getFavorites(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtConfig.extractUsername(token);

        List<Info> favorites = userService.getFavorites(username);

        return ResponseEntity.ok(favorites);
    }
}
