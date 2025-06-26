package com.ridenow.driverservice.domain;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String make;
    private String model;

    @Column(unique = true, nullable = false)
    private String plateNumber;
    private String type;
}
