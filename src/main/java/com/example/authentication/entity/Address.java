package com.example.authentication.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("address")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private String street;
    private String zipcode;
    private String city;
    private String county;
    private String country;
}
