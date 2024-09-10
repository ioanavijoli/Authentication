package com.example.authentication.mapper;


import com.example.authentication.dto.UserProfileDTO;
import com.example.authentication.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {

    public static UserProfileDTO convertToDTO(UserProfile userProfile) {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setFirstname(userProfile.getFirstname());
        userProfileDTO.setSurname(userProfile.getSurname());
        userProfileDTO.setBirthday(userProfile.getBirthday());
        userProfileDTO.setGender(userProfile.getGender());
        userProfileDTO.setSmallAvatar(userProfile.getSmallAvatar());
        userProfileDTO.setAddress(userProfile.getAddress());
        return userProfileDTO;
    }

    public static UserProfile convertToEntity(UserProfileDTO userProfileDTO) {
        UserProfile userProfile = new UserProfile();
        userProfile.setFirstname(userProfileDTO.getFirstname());
        userProfile.setSurname(userProfileDTO.getSurname());
        userProfile.setBirthday(userProfileDTO.getBirthday());
        userProfile.setGender(userProfileDTO.getGender());
        userProfile.setSmallAvatar(userProfileDTO.getSmallAvatar());
        userProfile.setAddress(userProfileDTO.getAddress());
        return userProfile;
    }
}
