package com.example.bitmani.carbook;

public class PlaceDetails {
    private String placename;
    private String latitude;
    private String longitude;
    private String address;

    public PlaceDetails(String placename, String latitude, String longitude, String address) {
        this.placename = placename;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public String getPlacename() {
        return placename;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }
}
