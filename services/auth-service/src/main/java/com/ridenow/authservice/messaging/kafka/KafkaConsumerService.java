package com.ridenow.authservice.messaging.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridenow.authservice.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @Autowired
    UserAuthService userAuthService;


    @KafkaListener(topics = "update-to-driver", groupId = "my-group")
    public void listenToDriverService(String userId) throws JsonProcessingException {
        userAuthService.updateStatusToDriver(Long.parseLong(userId));
    }
}
