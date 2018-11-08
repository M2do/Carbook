package com.example.bitmani.carbook;

public class CarRegisterData {
    private String email;
    private String ownerName;
    private String phoneNumber;
    private String outerImageUrl;

    public CarRegisterData() {
    }

    public CarRegisterData(String email, String ownerName, String phoneNumber, String outerImageUrl) {
        this.email = email;
        this.ownerName = ownerName;
        this.phoneNumber = phoneNumber;
        this.outerImageUrl = outerImageUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getOuterImageUrl() {
        return outerImageUrl;
    }
}
