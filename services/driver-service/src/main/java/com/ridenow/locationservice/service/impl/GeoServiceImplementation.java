package com.ridenow.locationservice.service.impl;

import com.ridenow.locationservice.service.GeoService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@Data
public class GeoServiceImplementation implements GeoService {
    private final GeoOperations<String, String> geoOps;

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
