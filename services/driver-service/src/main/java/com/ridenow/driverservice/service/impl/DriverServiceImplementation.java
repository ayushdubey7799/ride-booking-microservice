package com.ridenow.driverservice.service.impl;

import com.ridenow.driverservice.domain.Driver;
import com.ridenow.driverservice.domain.DriverStatus;
import com.ridenow.driverservice.domain.Vehicle;
import com.ridenow.driverservice.dto.*;
import com.ridenow.driverservice.messaging.kafka.KafkaProducerService;
import com.ridenow.driverservice.repository.DriverRepository;
import com.ridenow.driverservice.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
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

    public List<NearbyDriverResponse> getNearbyDrivers(double latitude, double longitude, double radiusKm,String vehicleType) {
        Circle circle = new Circle(new Point(longitude, latitude), new Distance(radiusKm, Metrics.KILOMETERS));

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redis.opsForGeo()
                .radius("drivers:geo", circle, RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance());

        if (results == null) return List.of();

        return results.getContent().stream().map(result -> {
            String key = result.getContent().getName(); // e.g., "driver:123"
            Long driverId = Long.parseLong(key.replace("driver:", ""));

            return new NearbyDriverResponse(driverId, result.getDistance().getValue());
        }).filter(resp -> {
            Optional<Driver> driver = driverRepository.findById(resp.getDriverId());
            return driver.filter(value -> value.getVehicle().getType().equalsIgnoreCase(vehicleType)).isPresent();
        }).collect(Collectors.toList());
    }



}
