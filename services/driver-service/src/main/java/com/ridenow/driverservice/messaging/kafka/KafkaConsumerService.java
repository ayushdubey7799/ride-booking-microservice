package com.ridenow.driverservice.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridenow.driverservice.domain.DriverStatus;
import com.ridenow.driverservice.dto.DriverOnlineRequest;
import com.ridenow.driverservice.dto.UpdateDriverStatusDto;
import com.ridenow.driverservice.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

public class KafkaConsumerService {

    @Autowired
    private DriverService driverService;

    @KafkaListener(topics = "update-driver-status-topic", groupId = "my-group")
    public void listenToBookingService(String updateDriverStatus) throws JsonProcessingException {
        System.out.println("Received message: " + updateDriverStatus);
        ObjectMapper mapper = new ObjectMapper();
        UpdateDriverStatusDto updateDriverStatusDto = mapper.readValue(updateDriverStatus, UpdateDriverStatusDto.class);
        if(updateDriverStatusDto.getStatus().equalsIgnoreCase("online")){
            driverService.markDriverOnline(updateDriverStatusDto.getId(), DriverOnlineRequest.newInstance(updateDriverStatusDto.getLongitude(),updateDriverStatusDto.getLatitude()));
        } else if (updateDriverStatusDto.getStatus().equalsIgnoreCase("busy")) {
            driverService.markDriverBusy(updateDriverStatusDto.getId());
        }
    }
}
