package com.example.authentication.services;

import com.example.authentication.entity.Info;
import com.example.authentication.entity.Review;
import com.example.authentication.entity.User;
import com.example.authentication.repositories.InfoRepository;
import com.example.authentication.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final InfoRepository infoRepository;

    @Autowired
    public UserService(UserRepository userRepository, InfoRepository infoRepository) {
        this.userRepository = userRepository;
        this.infoRepository = infoRepository;
    }

    public void createUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean checkPassword(String userPassword, String password) {
        return BCrypt.checkpw(password, userPassword);
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
