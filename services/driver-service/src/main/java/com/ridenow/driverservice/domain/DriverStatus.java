package com.ridenow.driverservice.domain;

public enum DriverStatus {
    ONLINE, OFFLINE, BUSY
}
// What if booking ends and driver marks them offline, but kafka message from booking service marks it online ?

