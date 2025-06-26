package com.ridenow.driverservice.service.impl;

import com.ridenow.driverservice.service.GeoService;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GeoServiceImplementation implements GeoService {
    private final GeoOperations<String, String> geoOps;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void testRedisConnection() {
        try {
            String pong = redisTemplate.getConnectionFactory().getConnection().ping();
            System.out.println("Redis ping response: " + pong);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public GeoServiceImplementation(StringRedisTemplate redisTemplate) {
        this.geoOps = redisTemplate.opsForGeo();
    }

    public void addLocation(String key, double longitude, double latitude, String member) {
        geoOps.add(key, new Point(longitude, latitude), member);
    }

    public Distance getDistance(String key, String member1, String member2) {
        return geoOps.distance(key, member1, member2, Metrics.KILOMETERS);
    }

    public List<String> getNearby(String key, double lon, double lat, double radiusKm) {
        Circle circle = new Circle(new Point(lon, lat), new Distance(radiusKm, Metrics.KILOMETERS));
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOps.radius(key, circle);
        return results.getContent().stream()
                .map(GeoResult::getContent)
                .map(RedisGeoCommands.GeoLocation::getName)
                .toList();
    }
}
