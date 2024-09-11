package com.example.authentication.services;

import com.example.authentication.entity.Info;
import com.example.authentication.entity.Review;
import com.example.authentication.repositories.InfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        review.setDatePosted(LocalDateTime.now());
        if(info.getReviews() == null) {
            info.setReviews(new ArrayList<>());
        }
        info.getReviews().add(review);
        infoRepository.save(info);
    }

    public List<Review> getReviewsForInfo(String infoId) {
        Info info = infoRepository.findById(infoId)
                .orElseThrow(() -> new IllegalStateException("Info not found"));
        return info.getReviews();
    }
}
