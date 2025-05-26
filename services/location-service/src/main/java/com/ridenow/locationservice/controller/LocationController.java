package com.ridenow.locationservice.controller;

import com.ridenow.locationservice.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/drivers")
public class LocationController {

    @Autowired
    LocationService locationService;

}
