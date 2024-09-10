package com.example.authentication.services;

import com.example.authentication.dto.UserProfileDTO;
import com.example.authentication.entity.User;
import com.example.authentication.entity.UserProfile;
import com.example.authentication.repositories.UserProfileRepository;
import com.example.authentication.util.ImageUtil;
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

        UserProfile userProfile = new UserProfile(user.getUUID(),
                userProfileDTO.getFirstname(),
                userProfileDTO.getSurname(),
                userProfileDTO.getBirthday(),
                userProfileDTO.getGender(),
                ImageUtil.compressImage(userProfileDTO.getSmallAvatar()));

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

        if (newProfile.getGender() != null && !Objects.equals(newProfile.getGender(), "")) {
            userProfile.setGender(newProfile.getGender());
        }
        if (newProfile.getSmallAvatar() != null && newProfile.getSmallAvatar().length > 0) {
            userProfile.setSmallAvatar(newProfile.getSmallAvatar());
        }
        userProfile.setDateUpdated(LocalDateTime.now());
        userProfileRepository.save(userProfile);
    }

    public UserProfileDTO getUserProfileDTO(String userUUID) {
        UserProfile userProfile = findByUserUUID(userUUID);
        if (userProfile == null) {
            return null;
        }

        byte[] decompressedImage = ImageUtil.decompressImage(userProfile.getSmallAvatar());

        return new UserProfileDTO(
                userProfile.getFirstname(),
                userProfile.getSurname(),
                userProfile.getBirthday(),
                decompressedImage,
                userProfile.getGender()
        );
    }
}
