package com.example.authentication.repositories;

import com.example.authentication.entity.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
    @Query("{userUUID: '?0'}")
    Optional<UserProfile> findByUUID(String uuid);
}
