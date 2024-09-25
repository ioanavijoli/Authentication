package com.example.authentication.services;

import com.example.authentication.entity.Info;
import com.example.authentication.entity.Review;
import com.example.authentication.entity.User;
import com.example.authentication.entity.UserProfile;
import com.example.authentication.repositories.InfoRepository;
import com.example.authentication.repositories.UserProfileRepository;
import com.example.authentication.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final InfoRepository infoRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserProfileRepository userProfileRepository, InfoRepository infoRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.infoRepository = infoRepository;
    }

    public void createUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<Info> getFavorites(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalStateException("User not found"));

        List<Info> favorites = new ArrayList<>();

        for (String infoId : user.getFavorites()) {
            Optional<Info> optionalInfo = infoRepository.findById(infoId);
            optionalInfo.ifPresent(favorites::add);
        }
        return favorites;
    }

    public Map<String, String> getUserDetailsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        UserProfile userProfile = userProfileRepository.findByUUID(user.getUUID())
                .orElseThrow(() -> new IllegalStateException("User profile not found"));

        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("firstName", userProfile.getFirstname());
        userDetails.put("lastName", userProfile.getSurname());

        return userDetails;
    }
    public void addToFavorites(String username, String infoId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalStateException("User not found"));

        if (user.getFavorites() == null) {
            user.setFavorites(new ArrayList<>());
        }

        if (user.getFavorites().contains(infoId)) {
            throw new IllegalStateException("Carehome is already in favorites");
        }
        user.getFavorites().add(infoId);
        userRepository.save(user);
    }

    public void removeFromFavorites(String username, String infoId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalStateException("User not found"));
        if (user.getFavorites() == null) {
            throw new IllegalStateException("No favorites found");
        }
        if (!user.getFavorites().contains(infoId)) {
            throw new IllegalStateException("Carehome not found in favorites");
        }

        user.getFavorites().remove(infoId);
        userRepository.save(user);
    }

    public List<Review> getReviewsByUser(String username) {
        List<Info> infos = infoRepository.findAll();

        return infos.stream()
                .filter(info -> info.getReviews() != null && !info.getReviews().isEmpty())
                .flatMap(info -> info.getReviews().stream())
                .filter(review -> review.getUsername().equals(username))
                .collect(Collectors.toList());
    }

}
