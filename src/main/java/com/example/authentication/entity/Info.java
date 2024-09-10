package com.example.authentication.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document ;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;

@Document(collection = "info")
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


    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrgID() {
        return orgID;
    }

    public void setOrgID(String orgID) {
        this.orgID = orgID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }
}
