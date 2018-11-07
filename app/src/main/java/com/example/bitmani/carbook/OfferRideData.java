package com.example.bitmani.carbook;

public class OfferRideData {
    private String email;
    private PlaceDetails sourcePlaceDetails;
    private PlaceDetails destinationPlaceDetails;
    private DateData date;
    private TimeData time;

    public OfferRideData() {
    }

    public OfferRideData(String email, PlaceDetails sourcePlaceDetails, PlaceDetails destinationPlaceDetails, DateData date, TimeData time) {
        this.email = email;
        this.sourcePlaceDetails = sourcePlaceDetails;
        this.destinationPlaceDetails = destinationPlaceDetails;
        this.date = date;
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public PlaceDetails getSourcePlaceDetails() {
        return sourcePlaceDetails;
    }

    public PlaceDetails getDestinationPlaceDetails() {
        return destinationPlaceDetails;
    }

    public DateData getDate() {
        return date;
    }

    public TimeData getTime() {
        return time;
    }
}
