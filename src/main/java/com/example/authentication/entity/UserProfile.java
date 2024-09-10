package com.example.authentication.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Column;
import javax.persistence.Lob;
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
    private String gender;
    @Lob
    @Column(length = 1000)
    private byte[] smallAvatar;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private boolean logicalDeleted;
    private String UUID;
    private Integer version;

    public UserProfile(String userUUID, String firstname, String surname, LocalDateTime birthday, String gender, byte[] smallAvatar) {
        this.userUUID = userUUID;
        this.firstname = firstname;
        this.surname = surname;
        this.birthday = birthday;
        this.gender = gender;
        this.smallAvatar = smallAvatar;
        this.dateCreated = LocalDateTime.now();
        this.logicalDeleted = false;
        this.UUID = java.util.UUID.randomUUID().toString();
        this.version = 0;
    }

}
