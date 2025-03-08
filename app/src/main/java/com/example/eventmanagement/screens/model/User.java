package com.example.eventmanagement.screens.model;

import com.google.firebase.firestore.Exclude;

public class User {
    @Exclude
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    public User() {
        // Required empty constructor for Firestore
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName != null ? firstName : "";
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName != null ? lastName : "";
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email != null ? email : "";
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone != null ? phone : "";
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Exclude
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}