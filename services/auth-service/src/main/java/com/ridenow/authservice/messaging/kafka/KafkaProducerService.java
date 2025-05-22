package com.ridenow.authservice.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridenow.authservice.domain.UserAuthEntity;
import com.ridenow.authservice.dto.UserAuthDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, UserAuthDto userAuthDto) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        kafkaTemplate.send(topic,mapper.writeValueAsString(userAuthDto));
    }
}
