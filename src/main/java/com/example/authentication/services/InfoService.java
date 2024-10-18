package com.example.authentication.services;

import com.example.authentication.entity.Info;
import com.example.authentication.entity.Review;
import com.example.authentication.repositories.InfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class InfoService {

    private final InfoRepository infoRepository;

    @Autowired
    public InfoService(InfoRepository infoRepository) {
        this.infoRepository = infoRepository;
    }

    public void addReview(String infoId, Review review) {
        Info info = infoRepository.findById(infoId)
                .orElseThrow(() -> new IllegalStateException("Info not found"));

        boolean alreadyReviewed = info.getReviews() != null && info.getReviews().stream()
                .anyMatch(existingReview -> review.getUsername() != null && existingReview.getUsername()!=null && existingReview.getUsername().equals(review.getUsername()));

        if (alreadyReviewed) {
            throw new IllegalStateException("User has already submitted a review for this info.");
        }

        review.setDatePosted(LocalDateTime.now());
        if (info.getReviews() == null) {
            info.setReviews(new ArrayList<>());
        }
        info.getReviews().add(review);
        infoRepository.save(info);
    }

    public void deleteReview(String infoId, String username) {
        Info info = infoRepository.findById(infoId)
                .orElseThrow(() -> new IllegalStateException("Info not found"));

        List<Review> updatedReviews = info.getReviews().stream()
                .filter(review -> review.getUsername() == null || !review.getUsername().equals(username))
                .collect(java.util.stream.Collectors.toList());

        if (updatedReviews.size() == info.getReviews().size()) {
            throw new IllegalStateException("Review by user not found for this info.");
        }

        info.setReviews(updatedReviews);
        infoRepository.save(info);
    }

    public Page<Review> getReviewsForInfo(String infoId, Pageable pageable) {
        Info info = infoRepository.findById(infoId)
                .orElseThrow(() -> new IllegalStateException("Info not found"));

        List<Review> reviews = info.getReviews();
        if (reviews == null || reviews.isEmpty()) {
            return Page.empty(pageable);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), reviews.size());

        List<Review> paginatedReviews = reviews.subList(start, end);

        return new PageImpl<>(paginatedReviews, pageable, reviews.size());
    }

    public boolean existsById(String infoId) {
        return infoRepository.existsById(infoId);
    }

}
