package com.example.authentication.services;

import com.example.authentication.dto.UserProfileDTO;
import com.example.authentication.entity.User;
import com.example.authentication.entity.UserProfile;
import com.example.authentication.repositories.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public void createProfile(UserProfileDTO userProfileDTO, User user) {
        if (userProfileRepository.findByUUID(user.getUUID()).isPresent()) {
            throw new IllegalStateException("You already have a profile.");
        }

        String userUUID = user.getUUID();
        String firstname = userProfileDTO.getFirstname();
        String surname = userProfileDTO.getSurname();
        LocalDateTime birthday = userProfileDTO.getBirthday();
        String religion = userProfileDTO.getReligion();
        String gender = userProfileDTO.getGender();
        String relationship = userProfileDTO.getRelationship();

        UserProfile userProfile  = new UserProfile(userUUID, firstname, surname, birthday, religion, gender, relationship);
        userProfileRepository.save(userProfile);
    }

    public UserProfile findByUserUUID(String uuid){
        return userProfileRepository.findByUUID(uuid).orElse(null);
    }

    public void updateProfile(UserProfileDTO newProfile, UserProfile userProfile) {
        if (newProfile.getFirstname() != null && !Objects.equals(newProfile.getFirstname(), "")) {
            userProfile.setFirstname(newProfile.getFirstname());
        }
        if (newProfile.getSurname() != null && !Objects.equals(newProfile.getSurname(), "")) {
            userProfile.setSurname(newProfile.getSurname());
        }
        if (newProfile.getBirthday() != null) {
            userProfile.setBirthday(newProfile.getBirthday());
        }
        if (newProfile.getReligion() != null && !Objects.equals(newProfile.getReligion(), "")) {
            userProfile.setReligion(newProfile.getReligion());
        }
        if (newProfile.getGender() != null && !Objects.equals(newProfile.getGender(), "")) {
            userProfile.setGender(newProfile.getGender());
        }
        if (newProfile.getRelationship() != null && !Objects.equals(newProfile.getRelationship(), "")) {
            userProfile.setRelationship(newProfile.getRelationship());
        }
        userProfile.setDateUpdated(LocalDateTime.now());
        userProfileRepository.save(userProfile);
    }
}
