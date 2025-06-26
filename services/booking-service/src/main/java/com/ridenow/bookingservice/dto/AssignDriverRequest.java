package com.ridenow.bookingservice.dto;

import lombok.Data;

@Data
public class AssignDriverRequest {
    private Double pickupLat;
    private Double pickupLng;
    private Double dropLat;
    private Double dropLng;
    private String vehicleType;
}

