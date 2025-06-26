package com.ridenow.driverservice.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;

    @Column(unique = true)
    private long userId;

    @Enumerated(EnumType.STRING)
    private DriverStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    private Vehicle vehicle;

}