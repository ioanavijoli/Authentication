package com.example.authentication.services;

import com.example.authentication.dto.UserProfileDTO;
import com.example.authentication.entity.Address;
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
                ImageUtil.compressImage(userProfileDTO.getSmallAvatar()),
                userProfileDTO.getAddress());

        userProfileRepository.save(userProfile);
    }


    public UserProfile findByUserUUID(String uuid) {
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
        if (newProfile.getAddress() != null) {
            Address address = userProfile.getAddress() != null ? userProfile.getAddress() : new Address();

            if (newProfile.getAddress().getStreet() != null && !Objects.equals(newProfile.getAddress().getStreet(), "")) {
                address.setStreet(newProfile.getAddress().getStreet());
            }
            if (newProfile.getAddress().getZipcode() != null && !Objects.equals(newProfile.getAddress().getZipcode(), "")) {
                address.setZipcode(newProfile.getAddress().getZipcode());
            }
            if (newProfile.getAddress().getCity() != null && !Objects.equals(newProfile.getAddress().getCity(), "")) {
                address.setCity(newProfile.getAddress().getCity());
            }
            if (newProfile.getAddress().getCounty() != null && !Objects.equals(newProfile.getAddress().getCounty(), "")) {
                address.setCounty(newProfile.getAddress().getCounty());
            }
            if (newProfile.getAddress().getCountry() != null && !Objects.equals(newProfile.getAddress().getCountry(), "")) {
                address.setCountry(newProfile.getAddress().getCountry());
            }

            userProfile.setAddress(address);
        }
        userProfile.setDateUpdated(LocalDateTime.now());
        userProfileRepository.save(userProfile);
    }

    private static Address getAddress(UserProfileDTO newProfile, UserProfile userProfile) {
        Address address = userProfile.getAddress() != null ? userProfile.getAddress() : new Address();

        if (newProfile.getAddress().getStreet() != null && !Objects.equals(newProfile.getAddress().getStreet(), "")) {
            address.setStreet(newProfile.getAddress().getStreet());
        }
        if (newProfile.getAddress().getZipcode() != null && !Objects.equals(newProfile.getAddress().getZipcode(), "")) {
            address.setZipcode(newProfile.getAddress().getZipcode());
        }
        if (newProfile.getAddress().getCity() != null && !Objects.equals(newProfile.getAddress().getCity(), "")) {
            address.setCity(newProfile.getAddress().getCity());
        }
        if (newProfile.getAddress().getCounty() != null && !Objects.equals(newProfile.getAddress().getCounty(), "")) {
            address.setCounty(newProfile.getAddress().getCounty());
        }
        if (newProfile.getAddress().getCountry() != null && !Objects.equals(newProfile.getAddress().getCountry(), "")) {
            address.setCountry(newProfile.getAddress().getCountry());
        }
        return address;
    }

    public UserProfileDTO getUserProfileDTO(String userUUID) {
        UserProfile userProfile = findByUserUUID(userUUID);
        if (userProfile == null) {
            return null;
        }

        byte[] decompressedImage = ImageUtil.decompressImage(userProfile.getSmallAvatar());
        System.out.println(userProfile.getAddress());
        return new UserProfileDTO(
                userProfile.getFirstname(),
                userProfile.getSurname(),
                userProfile.getBirthday(),
                decompressedImage,
                userProfile.getGender(),
                userProfile.getAddress()
        );
    }
}
