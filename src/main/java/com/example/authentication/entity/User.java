package com.example.authentication.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.List;

@Document("users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private ObjectId id;
    private String username;
    private String email;
    private String password;
    private String salt;
    private UserRole role;
    private LocalDateTime dateCreated;
    private boolean logicalDeleted;
    private String UUID;
    private Integer version;
    private List<String> favorites;

    public User(String username, String email, String password, UserRole role) {
        super();
        this.username = username;
        this.email = email;
        this.salt = BCrypt.gensalt();
        this.password = BCrypt.hashpw(password, salt);
        this.role = role;
        this.dateCreated = LocalDateTime.now();
        this.logicalDeleted = false;
        this.UUID = java.util.UUID.randomUUID().toString();
        this.version = 0;
    }


}
