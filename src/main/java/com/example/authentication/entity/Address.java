package com.example.authentication.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "address")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @Field("Country")
    private String country;

    @Field("State")
    private String state;

    @Field("City")
    private String city;

    @Field("PostalCode")
    private String postalCode;

    @Field("Street")
    private String street;

    @Field("County")
    private String county;

    @Field("Number")
    private String number;

}
