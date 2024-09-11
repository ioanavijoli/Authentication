package com.example.authentication.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "info")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Info {
    @Id
    private String id;

    @Field("OrgID")
    private String orgID;

    @Field("Name")
    private String name;

    @Field("Email")
    private String email;

    @Field("Description")
    private String description;

    @Field("PhoneNumber")
    private String phoneNumber;

    @Field("Address")
    private Address address;

    @Field("Services")
    private List<String> services;

    @Field("Images")
    private List<String> images;

    @Field("Reviews")
    private List<Review> reviews;

}
