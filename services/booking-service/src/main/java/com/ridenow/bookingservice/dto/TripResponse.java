package com.ridenow.bookingservice.dto;


import lombok.Data;

@Data
public class TripResponse {
    private Long bookingId;
    private String status;
    private Double fare;
    private Double distanceKm;
    private long durationMinutes;

    public TripResponse(Long id, String status, Double fare, Double distanceKm, long duration) {
        this.bookingId = id;
        this.status = status;
        this.fare = fare;
        this.distanceKm = distanceKm;
        this.durationMinutes = duration;
    }
}

