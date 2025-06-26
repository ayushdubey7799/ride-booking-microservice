package com.ridenow.driverservice.service;

import com.ridenow.driverservice.dto.DriverApplyRequest;
import com.ridenow.driverservice.dto.DriverOnlineRequest;
import com.ridenow.driverservice.dto.NearbyDriverResponse;
import com.ridenow.driverservice.dto.UpdateDriverStatusDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DriverService {
    void applyAsDriver(Long userId, DriverApplyRequest request);

    void markDriverOnline(Long userId, DriverOnlineRequest request);

    void markDriverOffline(Long userId);

    void markDriverBusy(Long driverId);

    List<NearbyDriverResponse> getNearbyDrivers(double latitude, double longitude, double radiusKm, String vehicleType);

}
