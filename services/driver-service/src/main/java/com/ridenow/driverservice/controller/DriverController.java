package com.ridenow.driverservice.controller;

import com.ridenow.driverservice.dto.DriverApplyRequest;
import com.ridenow.driverservice.dto.DriverOnlineRequest;
import com.ridenow.driverservice.dto.NearbyDriverResponse;
import com.ridenow.driverservice.service.DriverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    @Autowired
    DriverService driverService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyAsDriver(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String roles,
            @RequestBody DriverApplyRequest request
    ) {
        List<String> roleList = List.of(roles.split(","));

        if (!roleList.contains("RIDER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only users with RIDER role can apply as drivers.");
        }


        driverService.applyAsDriver(Long.valueOf(userId), request);

        return ResponseEntity.ok("Driver application successful. Awaiting approval.");
    }

    @PatchMapping("/status/online")
    public ResponseEntity<?> markOnline(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody DriverOnlineRequest request
    ) {
        driverService.markDriverOnline(Long.valueOf(userId), request);
        return ResponseEntity.ok("Driver marked as ONLINE.");
    }

    @PatchMapping("/status/offline")
    public ResponseEntity<?> markOffline(@RequestHeader("X-User-Id") String userId) {
        driverService.markDriverOffline(Long.valueOf(userId));
        return ResponseEntity.ok("Driver marked as OFFLINE.");
    }

    // shpuld be protected only use for booking service
    @PatchMapping("/{driverId}/status/busy")
    public ResponseEntity<?> markBusy(@PathVariable String driverId) {
        driverService.markDriverBusy(Long.valueOf(driverId));
        return ResponseEntity.ok("Driver marked as BUSY.");
    }



    @GetMapping("/nearby")
    public ResponseEntity<List<NearbyDriverResponse>> getNearbyDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5") double radiusKm,
            @RequestParam String vehicleType
    ) {
        List<NearbyDriverResponse> drivers = driverService.getNearbyDrivers(latitude, longitude, radiusKm,vehicleType);
        return ResponseEntity.ok(drivers);
    }



}
