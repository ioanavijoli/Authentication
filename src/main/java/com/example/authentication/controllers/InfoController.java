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
import java.util.stream.Collectors;

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
            if (e.getMessage().equals("User has already submitted a review for this info.")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User has already submitted a review for this info.");
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("message", e.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

        }
    }

    @GetMapping("/{infoId}/reviews")
    public ResponseEntity<List<Map<String, Object>>> getReviewsForInfo(@PathVariable String infoId) {
        try {
            List<Review> reviews = infoService.getReviewsForInfo(infoId);
            List<Map<String, Object>> reviewsWithUserDetails = reviews.stream().map(review -> {
                Map<String, Object> reviewData = new HashMap<>();
                reviewData.put("rating", review.getRating());
                reviewData.put("comment", review.getComment());
                reviewData.put("username", review.getUsername());
                reviewData.put("datePosted", review.getDatePosted());

                Map<String, String> userDetails = userService.getUserDetailsByUsername(review.getUsername());
                reviewData.put("firstName", userDetails.get("firstName"));
                reviewData.put("lastName", userDetails.get("lastName"));
                return reviewData;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(reviewsWithUserDetails);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{infoId}/review")
    public ResponseEntity<?> deleteReview(
            @PathVariable String infoId,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtConfig.extractUsername(token);

        try {
            infoService.deleteReview(infoId, username);
            return ResponseEntity.ok().body("Review deleted successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
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
