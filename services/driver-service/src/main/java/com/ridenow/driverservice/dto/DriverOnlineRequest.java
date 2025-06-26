package com.ridenow.driverservice.dto;

import lombok.Data;

@Data
public class DriverOnlineRequest {
    private double latitude;
    private double longitude;

    public static DriverOnlineRequest newInstance(double longitude, double latitude) {
        DriverOnlineRequest request = new DriverOnlineRequest();
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        return request;
    }
}

