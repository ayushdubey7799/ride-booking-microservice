package com.ridenow.bookingservice.repository;

import com.ridenow.bookingservice.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}

