package com.ridenow.bookingservice.dto;

import lombok.Data;

@Data
public class MatchingResultDto {
    private String status;
    private Long driverId;
    private String userId;
    private Long bookingId;
}
