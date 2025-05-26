package com.ridenow.locationservice.repository;

import com.ridenow.locationservice.domain.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByUsername(String username);

    Optional<Driver> findByUserId(Long userId);
}