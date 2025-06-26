package com.ridenow.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UpdateDriverStatusDto {
    private Long id;
    private String status;
    private double latitude;
    private double longitude;

    public static UpdateDriverStatusDto newInstance(Long driverId, String status, TripEndRequest request) {
        UpdateDriverStatusDto updateDriverStatusDto = new UpdateDriverStatusDto();
        updateDriverStatusDto.setId(driverId);
        updateDriverStatusDto.setStatus(status);
        if(request!=null){
            updateDriverStatusDto.setLatitude(request.getDropLat());
            updateDriverStatusDto.setLongitude(request.getDropLat());
        }
        return updateDriverStatusDto;
    }
}
