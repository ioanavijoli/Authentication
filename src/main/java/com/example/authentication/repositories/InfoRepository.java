package com.example.authentication.repositories;


import com.example.authentication.entity.Info;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InfoRepository extends MongoRepository<Info, String> {
    Optional<Info> findById(String id);
}
