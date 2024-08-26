package com.example.authentication.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("userProfiles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {

    @Id
    private ObjectId id;
    private String userUUID;
    private String firstname;
    private String surname;
    private LocalDateTime birthday;
    private String religion;
    private String gender;
    private String relationship;
    private byte[] smallAvatar;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private boolean logicalDeleted;
    private String UUID;
    private Integer version;

    public UserProfile(String userUUID, String firstname, String surname, LocalDateTime birthday, String religion, String gender, String relationship) {
        this.userUUID = userUUID;
        this.firstname = firstname;
        this.surname = surname;
        this.birthday = birthday;
        this.religion = religion;
        this.gender = gender;
        this.relationship = relationship;
        this.dateCreated = LocalDateTime.now();
        this.logicalDeleted = false;
        this.UUID = java.util.UUID.randomUUID().toString();
        this.version = 0;
    }

}
