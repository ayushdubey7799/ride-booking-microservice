package com.ridenow.bookingservice.controller;


import com.ridenow.bookingservice.domain.Booking;
import com.ridenow.bookingservice.dto.AssignDriverRequest;
import com.ridenow.bookingservice.dto.TripEndRequest;
import com.ridenow.bookingservice.dto.TripResponse;
import com.ridenow.bookingservice.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/assign-driver")
    public ResponseEntity<Booking> assignDriver(@RequestHeader("X-User-Id") String userId,@RequestBody AssignDriverRequest request) {
        return ResponseEntity.ok(bookingService.assignDriver(Long.valueOf(userId),request));
    }

    @PostMapping("/start-trip/{tripId}")
    public ResponseEntity<String> startTrip(@PathVariable String tripId) {
        bookingService.startTrip(tripId);
        return ResponseEntity.ok("Trip started");
    }

    @PostMapping("/end-trip")
    public ResponseEntity<TripResponse> endTrip(@RequestBody TripEndRequest request) {
        TripResponse response = bookingService.endTrip(request);
        return ResponseEntity.ok(response);
    }
}

