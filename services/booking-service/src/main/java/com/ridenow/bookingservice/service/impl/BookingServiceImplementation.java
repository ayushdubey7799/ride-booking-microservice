package com.ridenow.bookingservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridenow.bookingservice.domain.Booking;
import com.ridenow.bookingservice.dto.*;
import com.ridenow.bookingservice.messaging.kafka.KafkaProducerService;
import com.ridenow.bookingservice.repository.BookingRepository;
import com.ridenow.bookingservice.service.BookingService;
import com.ridenow.bookingservice.utils.DistanceUtil;
import com.ridenow.bookingservice.utils.FareUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
public class BookingServiceImplementation implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Override
    public Booking assignDriver(Long userId,AssignDriverRequest request) {
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setPickupLat(request.getPickupLat());
        booking.setPickupLng(request.getPickupLng());
        booking.setDropLat(request.getDropLat());
        booking.setDropLng(request.getDropLng());
        booking.setStatus("ASSIGNED");
        booking.setStartTime(null);
        booking.setEndTime(null);
        booking.setVehicleType(request.getVehicleType());
        booking = bookingRepository.save(booking);
        ObjectMapper mapper = new ObjectMapper();
        try {
            kafkaProducerService.sendMessage("ride-matching-topic",mapper.writeValueAsString(booking));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return booking;
    }

    @Override
    public void startTrip(String bookingId) {
        Booking booking = bookingRepository.findById(Long.parseLong(bookingId))
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus("ONGOING");
        booking.setStartTime(LocalDateTime.now());
        bookingRepository.save(booking);

        ObjectMapper mapper = new ObjectMapper();
        try{
            String payload = mapper.writeValueAsString(UpdateDriverStatusDto.newInstance(booking.getDriverId(),"online",null ));
            kafkaProducerService.sendMessage("update-driver-status-topic",payload );
        }
        catch (JsonProcessingException e){
            log.error(e.getMessage());
        }
    }

    @Override
    public TripResponse endTrip(TripEndRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setEndTime(LocalDateTime.now());
        booking.setDropLat(request.getDropLat());
        booking.setDropLng(request.getDropLng());
        booking.setStatus("COMPLETED");

        double distanceKm = DistanceUtil.calculate(
                booking.getPickupLat(), booking.getPickupLng(),
                request.getDropLat(), request.getDropLng()
        );

        double fare = FareUtil.calculateFare(distanceKm);

        booking.setDistanceKm(distanceKm);
        booking.setFare(fare);

        bookingRepository.save(booking);

        // Optional: Notify driverService that driver is available again
        ObjectMapper mapper = new ObjectMapper();
        try{
            String payload = mapper.writeValueAsString(UpdateDriverStatusDto.newInstance(booking.getDriverId(),"online",request ));
            kafkaProducerService.sendMessage("update-driver-status-topic",payload );
        }
        catch (JsonProcessingException e){
            log.error(e.getMessage());
        }

        return new TripResponse(
                booking.getId(),
                booking.getStatus(),
                fare,
                distanceKm,
                Duration.between(booking.getStartTime(), booking.getEndTime()).toMinutes()
        );
    }

    @Override
    public void updateMatchResult(MatchingResultDto matchingResultDto) {
      log.info("=====> " + matchingResultDto.getDriverId());
        log.info("=====> " + matchingResultDto.getStatus());

    }
}
