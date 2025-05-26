package com.ridenow.locationservice.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridenow.locationservice.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @Autowired
    LocationService locationService;

    @KafkaListener(topics = "", groupId = "my-group")
    public void listen(String userAuthString) throws JsonProcessingException {
        System.out.println("Received message: " + userAuthString);
        ObjectMapper mapper = new ObjectMapper();
//        UserAuthDto userAuthDto = mapper.readValue(userAuthString, UserAuthDto.class);
//        userService.createProfile(userAuthDto);
    }
}
