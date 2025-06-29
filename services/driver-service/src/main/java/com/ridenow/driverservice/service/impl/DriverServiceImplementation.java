package com.ridenow.driverservice.service.impl;

import com.ridenow.driverservice.domain.Driver;
import com.ridenow.driverservice.domain.DriverStatus;
import com.ridenow.driverservice.domain.Vehicle;
import com.ridenow.driverservice.dto.*;
import com.ridenow.driverservice.messaging.kafka.KafkaProducerService;
import com.ridenow.driverservice.repository.DriverRepository;
import com.ridenow.driverservice.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
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
        driver.setLatitude(request.getLatitude());
        driver.setLongitude(request.getLongitude());
        driverRepository.save(driver);

        // Redis GEO
        Point point = new Point(request.getLongitude(), request.getLatitude());
        redis.opsForGeo().add("drivers:geo", point, "driver:" + driver.getId());

        // Store driver metadata (optional)
        Map<String, String> metadata = new HashMap<>();
        metadata.put("status", "ONLINE");
        metadata.put("vehicleType", driver.getVehicle() != null ? driver.getVehicle().getType() : "UNKNOWN");
        metadata.put("lat", String.valueOf(request.getLatitude()));
        metadata.put("lng", String.valueOf(request.getLongitude()));

        redis.opsForHash().putAll("driver:" + driver.getId(), metadata);
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

//    @EventListener(ApplicationReadyEvent.class)
    public void refreshDriverRedisCache() {
        // Clear existing Redis entries
        redis.delete("drivers:geo");

        List<Driver> drivers = driverRepository.findAll();

        for (Driver driver : drivers) {
            String key = "driver:" + driver.getId();

            // Generate random coordinates in Sector 70, Noida
            double lat = 28.6035 + Math.random() * (28.6075 - 28.6035);
            double lng = 77.3635 + Math.random() * (77.3685 - 77.3635);

            // Update DB for tracking (optional but recommended)
            driver.setLatitude(lat);
            driver.setLongitude(lng);
            driverRepository.save(driver);

            // Add to Redis GEO
            Point point = new Point(lng, lat);
            redis.opsForGeo().add("drivers:geo", point, key);

            // Metadata (e.g., vehicle type, status)
            Map<String, String> metadata = new HashMap<>();
            metadata.put("vehicleType", driver.getVehicle() != null ? driver.getVehicle().getType() : "UNKNOWN");
            metadata.put("status", driver.getStatus() != null ? driver.getStatus().name() : "UNKNOWN");
            metadata.put("lat", String.valueOf(lat));
            metadata.put("lng", String.valueOf(lng));

            redis.opsForHash().putAll(key, metadata);
        }

        System.out.println("✅ Redis refreshed with driver data and randomized coordinates in Sector 70, Noida");
    }


//    public List<Map<String, String>> getAllDriversInRedis() {
//        GeoOperations<String, String> geoOps = redis.opsForGeo();
//        HashOperations<String, String, String> hashOps = redis.opsForHash();
//
//        Set<String> driverIds = Optional.ofNullable(
//                        geoOps.radius("drivers:geo", new org.springframework.data.redis.connection.RedisGeoCommands.Circle(
//                                new org.springframework.data.geo.Point(0, 0), // get all points globally
//                                new org.springframework.data.geo.Distance(20000, org.springframework.data.geo.Metrics.KILOMETERS)))
//                )
//                .map(radius -> radius.getContent().stream().map(GeoLocation::getName).collect(Collectors.toSet()))
//                .orElse(Collections.emptySet());
//
//        List<Map<String, String>> driverDetails = new ArrayList<>();
//
//        for (String redisKey : driverIds) {
//            Map<String, String> details = hashOps.entries(redisKey);
//            details.put("driverKey", redisKey);
//            driverDetails.add(details);
//        }
//
//        return driverDetails;
//    }



}
