package com.example.authentication.controllers;

import com.example.authentication.config.JwtUtils;
import com.example.authentication.entity.Info;
import com.example.authentication.services.InfoService;
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
    private final InfoService infoService;

    @Autowired
    public UserController(UserService userService, JwtUtils jwtConfig, InfoService infoService) {
        this.userService = userService;
        this.jwtConfig = jwtConfig;
        this.infoService = infoService;
    }

    @PostMapping("/favorites/{infoId}")
    public ResponseEntity<?> addFavorite(@PathVariable String infoId, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtConfig.extractUsername(token);

        if (!infoService.existsById(infoId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errorCode", HttpStatus.NOT_FOUND.value(),
                            "errorKey", "INFO_NOT_FOUND",
                            "errorMsg", "No carehome found with the given ID."));
        }

        try {
            userService.addToFavorites(username, infoId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Carehome added to favorites");
            response.put("infoId", infoId);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("errorCode", HttpStatus.CONFLICT.value(),
                            "errorKey", "ALREADY_ADDED",
                            "errorMsg", e.getMessage(),
                            "infoId", infoId));
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errorCode", HttpStatus.NOT_FOUND.value(),
                            "errorKey", "CAREHOME_NOT_FOUND",
                            "errorMsg", e.getMessage()));
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
