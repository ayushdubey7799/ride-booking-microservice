package com.ridenow.locationservice.service;

import com.ridenow.locationservice.dto.DriverApplyRequest;
import com.ridenow.locationservice.dto.DriverOnlineRequest;

public interface DriverService {
    void applyAsDriver(Long userId, DriverApplyRequest request);

    void markDriverOnline(Long userId, DriverOnlineRequest request);

    void markDriverOffline(Long userId);

    void markDriverBusy(Long driverId);
}
