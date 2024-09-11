package com.example.authentication.controllers;

import com.example.authentication.entity.Review;
import com.example.authentication.services.InfoService;
import com.example.authentication.services.UserService;
import com.example.authentication.config.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/info")
public class InfoController {

    private final InfoService infoService;
    private final UserService userService;
    private final JwtUtils jwtConfig;

    @Autowired
    public InfoController(InfoService infoService, UserService userService, JwtUtils jwtConfig) {
        this.infoService = infoService;
        this.userService = userService;
        this.jwtConfig = jwtConfig;
    }

    @PostMapping("/{infoId}/review")
    public ResponseEntity<?> addReview(
            @PathVariable String infoId,
            @RequestBody Review review,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization").substring(7);
        String username = jwtConfig.extractUsername(token);
        review.setUsername(username);

        if (review.getRating() < 1 || review.getRating() > 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rating must be between 1 and 5.");
        }

        try {
            infoService.addReview(infoId, review);
            return ResponseEntity.ok(review);

        } catch (IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }


    @GetMapping("/{infoId}/reviews")
    public ResponseEntity<List<Review>> getReviewsForInfo(@PathVariable String infoId) {
        try {
            List<Review> reviews = infoService.getReviewsForInfo(infoId);
            return ResponseEntity.ok(reviews);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/user-reviews")
    public ResponseEntity<List<Review>> getReviewsByUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtConfig.extractUsername(token);

        List<Review> userReviews = userService.getReviewsByUser(username);
        return ResponseEntity.ok(userReviews);
    }
}
