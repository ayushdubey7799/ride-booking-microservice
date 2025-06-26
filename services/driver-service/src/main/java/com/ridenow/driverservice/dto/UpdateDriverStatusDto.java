package com.ridenow.driverservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateDriverStatusDto {
    private Long id;
    private String status;
    private double latitude;
    private double longitude;
}
