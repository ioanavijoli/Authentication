package com.example.authentication.controllers;

import com.example.authentication.config.JwtUtils;
import com.example.authentication.entity.Review;
import com.example.authentication.entity.User;
import com.example.authentication.services.InfoService;
import com.example.authentication.services.UserProfileService;
import com.example.authentication.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final UserProfileService userProfileService;
    private final JwtUtils jwtConfig;

    @Autowired
    public InfoController(InfoService infoService, UserService userService, UserProfileService userProfileService, JwtUtils jwtConfig) {
        this.infoService = infoService;
        this.userService = userService;
        this.userProfileService = userProfileService;
        this.jwtConfig = jwtConfig;
    }

    @PostMapping("/{infoId}/review")
    public ResponseEntity<?> addReview(
            @PathVariable String infoId,
            @RequestBody Review review,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization") != null ?
                request.getHeader("Authorization").substring(7) : null;
        String username;

        if (review.getRating() < 1 || review.getRating() > 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("errorCode", HttpStatus.BAD_REQUEST.value(),
                            "errorKey", "INVALID_RATING",
                            "errorMsg", "Rating must be between 1 and 5."));
        }

        if (review.getRating() < 3) {
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("errorCode", HttpStatus.UNAUTHORIZED.value(),
                                "errorKey", "AUTH_REQUIRED",
                                "errorMsg", "You must be logged in to leave a negative review."));
            }

            username = jwtConfig.extractUsername(token);
            review.setUsername(username);
            User user = userService.findByUsername(username).orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("errorCode", HttpStatus.NOT_FOUND.value(),
                                "errorKey", "USER_NOT_FOUND",
                                "errorMsg", "User not found"));
            }
            boolean hasCompleteProfile = userProfileService.hasCompleteProfile(user.getUUID());
            if (!hasCompleteProfile) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("errorCode", HttpStatus.BAD_REQUEST.value(),
                                "errorKey", "INCOMPLETE_PROFILE",
                                "errorMsg", "You must have a complete profile with name, date of birth, and contact details to leave a negative review."));
            }
        } else {
            if (token != null) {
                username = jwtConfig.extractUsername(token);
                review.setUsername(username);
            }
            else {
                review.setUsername(null); // ca nu sunt logat si o sa se posteze ca anonim
            }
        }

        try {
            infoService.addReview(infoId, review);
            return ResponseEntity.ok(review);
        } catch (IllegalStateException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            if (e.getMessage().equals("User has already submitted a review for this info.")) {
                errorResponse.put("errorCode", HttpStatus.CONFLICT.value());
                errorResponse.put("errorKey", "USR_REVIEW_DUPLICATE");
                errorResponse.put("errorMsg", "User has already submitted a review for this carehome.");
            } else {
                errorResponse.put("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
                errorResponse.put("errorKey", "INTERNAL_SERVER_ERROR");
                errorResponse.put("errorMsg", e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



    @GetMapping("/{infoId}/reviews")
    public ResponseEntity<Map<String, Object>> getReviewsForInfo(
            @PathVariable String infoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize);
        if(!infoService.existsById(infoId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errorCode", HttpStatus.NOT_FOUND.value(),
                            "errorKey", "CAREHOME_NOT_FOUND",
                            "errorMsg", "Carehome with given ID was not found."));
        }

        try {
            Page<Review> reviewsPage = infoService.getReviewsForInfo(infoId, pageable);

            List<Map<String, Object>> reviewsWithUserDetails = reviewsPage.getContent().stream().map(review -> {
                Map<String, Object> reviewData = new HashMap<>();

                reviewData.put("rating", review.getRating());
                reviewData.put("comment", review.getComment());
                reviewData.put("datePosted", review.getDatePosted());

                if (review.getUsername() != null && !review.getUsername().isEmpty()) {
                    Map<String, String> userDetails = userService.getUserDetailsByUsername(review.getUsername());
                    reviewData.put("username", review.getUsername());
                    reviewData.put("firstName", userDetails.get("firstName"));
                    reviewData.put("lastName", userDetails.get("lastName"));
                } else {
                    reviewData.put("username", "Anonymous");
                }

                return reviewData;
            }).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("pageSize", reviewsPage.getSize());
            response.put("page", reviewsPage.getNumber());
            response.put("items", reviewsWithUserDetails);

            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errorCode", HttpStatus.NOT_FOUND.value(),
                            "errorKey", "REVIEW_NOT_FOUND",
                            "errorMsg", e.getMessage()));
        }
    }

    @DeleteMapping("/{infoId}/review")
    public ResponseEntity<?> deleteReview(
            @PathVariable String infoId,
            HttpServletRequest request) {
        if(request.getHeader("Authorization") == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "errorCode", HttpStatus.FORBIDDEN.value(),
                            "errorKey", "AUTH_REQUIRED",
                            "errorMsg", "Authorization token is required to delete a review."
                    ));
        }
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtConfig.extractUsername(token);

        try {
            infoService.deleteReview(infoId, username);
            return ResponseEntity.ok().body("Review deleted successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("errorCode", HttpStatus.NOT_FOUND.value(),
                            "errorKey", "USR_REVIEW_NOT_FOUND",
                            "errorMsg", e.getMessage()));
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
