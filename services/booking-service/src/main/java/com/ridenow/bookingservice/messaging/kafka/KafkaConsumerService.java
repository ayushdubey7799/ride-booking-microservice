package com.ridenow.bookingservice.messaging.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridenow.bookingservice.dto.MatchingResultDto;
import com.ridenow.bookingservice.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @Autowired
    private BookingService bookingService;

    @KafkaListener(topics = "ride-matching-result-topic", groupId = "realtime-gateway")
    public void listenToMatchingService(String payload) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        MatchingResultDto matchingResultDto = mapper.convertValue(payload, MatchingResultDto.class);
        bookingService.updateMatchResult(matchingResultDto);
    }
}

