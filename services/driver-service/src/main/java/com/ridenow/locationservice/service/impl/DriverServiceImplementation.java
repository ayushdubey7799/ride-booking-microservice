package com.ridenow.locationservice.service.impl;

import com.ridenow.locationservice.domain.Driver;
import com.ridenow.locationservice.domain.DriverStatus;
import com.ridenow.locationservice.domain.Vehicle;
import com.ridenow.locationservice.dto.DriverApplyRequest;
import com.ridenow.locationservice.dto.DriverOnlineRequest;
import com.ridenow.locationservice.dto.VehicleDto;
import com.ridenow.locationservice.messaging.kafka.KafkaProducerService;
import com.ridenow.locationservice.repository.DriverRepository;
import com.ridenow.locationservice.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.StringRedisTemplate;

public class DriverServiceImplementation implements DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private StringRedisTemplate redis;

    @Override
    public void applyAsDriver(Long userId, DriverApplyRequest request) {
        VehicleDto v = request.getVehicle();

        Vehicle vehicle = new Vehicle();
        vehicle.setMake(v.getMake());
        vehicle.setModel(v.getModel());
        vehicle.setPlateNumber(v.getPlateNumber());
        vehicle.setType(v.getType());

        Driver driver = new Driver();
        driver.setUserId(userId);
        driver.setVehicle(vehicle);
        driver.setStatus(DriverStatus.OFFLINE);

        driverRepository.save(driver);
        kafkaProducerService.sendMessage("update-to-driver", String.valueOf(userId));
        // Optional: Call User Service to update role to DRIVER
    }

    @Override
    public void markDriverOnline(Long userId, DriverOnlineRequest request) {
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setStatus(DriverStatus.ONLINE);
        driverRepository.save(driver);

        // Add to Redis GEO
        Point point = new Point(request.getLongitude(), request.getLatitude());
        redis.opsForGeo().add("drivers:geo", point, "driver:" + driver.getId());
    }

    @Override
    public void markDriverOffline(Long userId) {
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setStatus(DriverStatus.OFFLINE);
        driverRepository.save(driver);

        // Remove from Redis GEO
        redis.opsForGeo().remove("drivers:geo", "driver:" + driver.getId());

    }

    @Override
    public void markDriverBusy(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setStatus(DriverStatus.BUSY);
        driverRepository.save(driver);

        redis.opsForGeo().remove("drivers:geo", "driver:" + driver.getId());
    }

}
