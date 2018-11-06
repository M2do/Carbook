package com.example.bitmani.carbook;

public class OfferRideData {
    private PlaceDetails sourcePlaceDetails;
    private PlaceDetails destinationPlaceDetails;
    private DateData date;
    private TimeData time;

    public OfferRideData(PlaceDetails sourcePlaceDetails, PlaceDetails destinationPlaceDetails, DateData date, TimeData time) {
        this.sourcePlaceDetails = sourcePlaceDetails;
        this.destinationPlaceDetails = destinationPlaceDetails;
        this.date = date;
        this.time = time;
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
