package com.ridenow.locationservice.controller;

import com.ridenow.locationservice.dto.DriverApplyRequest;
import com.ridenow.locationservice.dto.DriverOnlineRequest;
import com.ridenow.locationservice.service.DriverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    @Autowired
    DriverService driverService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyAsDriver(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestBody DriverApplyRequest request
    ) {
        if (!role.equalsIgnoreCase("RIDER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only users with RIDER role can apply as drivers.");
        }

        driverService.applyAsDriver(userId, request);

        return ResponseEntity.ok("Driver application successful. Awaiting approval.");
    }

    @PatchMapping("/status/online")
    public ResponseEntity<?> markOnline(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody DriverOnlineRequest request
    ) {
        driverService.markDriverOnline(userId, request);
        return ResponseEntity.ok("Driver marked as ONLINE.");
    }

    @PatchMapping("/status/offline")
    public ResponseEntity<?> markOffline(@RequestHeader("X-User-Id") Long userId) {
        driverService.markDriverOffline(userId);
        return ResponseEntity.ok("Driver marked as OFFLINE.");
    }

    // shpuld be protected only use for booking service
    @PatchMapping("/{driverId}/status/busy")
    public ResponseEntity<?> markBusy(@PathVariable Long driverId) {
        driverService.markDriverBusy(driverId);
        return ResponseEntity.ok("Driver marked as BUSY.");
    }





}
