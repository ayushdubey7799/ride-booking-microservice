package com.ridenow.bookingservice.dto;

import lombok.Data;

@Data
public class TripEndRequest {
    private Long bookingId;
    private Double dropLat;
    private Double dropLng;
}

