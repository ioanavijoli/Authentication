package com.example.authentication.dto;

import com.example.authentication.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private String firstname;
    private String surname;
    private LocalDateTime birthday;
    private byte[] smallAvatar;
    private String gender;
    private Address address;
}
