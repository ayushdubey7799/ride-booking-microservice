package com.ridenow.bookingservice.service;

import com.ridenow.bookingservice.domain.Booking;
import com.ridenow.bookingservice.dto.AssignDriverRequest;
import com.ridenow.bookingservice.dto.MatchingResultDto;
import com.ridenow.bookingservice.dto.TripEndRequest;
import com.ridenow.bookingservice.dto.TripResponse;

public interface BookingService {
    Booking assignDriver(Long userId,AssignDriverRequest request);
    void startTrip(String bookingId);
    TripResponse endTrip(TripEndRequest request);

    void updateMatchResult(MatchingResultDto matchingResultDto);
}

