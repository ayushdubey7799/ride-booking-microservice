package com.ridenow.bookingservice.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long driverId;

    private Double pickupLat;
    private Double pickupLng;

    private Double dropLat;
    private Double dropLng;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String vehicleType;

    private String status; // PENDING, ASSIGNED, ONGOING, COMPLETED

    private Double distanceKm;
    private Double fare;

}

