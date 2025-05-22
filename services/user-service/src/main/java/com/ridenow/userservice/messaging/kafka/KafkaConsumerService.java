package com.ridenow.userservice.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridenow.userservice.dto.UserAuthDto;
import com.ridenow.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @Autowired
    UserService userService;

    @KafkaListener(topics = "create-profile-topic", groupId = "my-group")
    public void listen(String userAuthString) throws JsonProcessingException {
        System.out.println("Received message: " + userAuthString);
        ObjectMapper mapper = new ObjectMapper();
        UserAuthDto userAuthDto = mapper.readValue(userAuthString, UserAuthDto.class);
        userService.createProfile(userAuthDto);
    }
}
