package com.ridenow.gatewayservice.config;

import com.ridenow.gatewayservice.filter.JwtAuthFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder, JwtAuthFilter jwtFilter) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/auth/**")
                        .uri("http://auth-service:8081"))
                .route("user-service", r -> r.path("/api/users/**")
                        .filters(f -> f.filter(jwtFilter.apply(new JwtAuthFilter.Config())))
                        .uri("http://user-service:8082"))
                .route("booking-service", r -> r.path("/api/bookings/**")
                        .filters(f -> f.filter(jwtFilter.apply(new JwtAuthFilter.Config())))
                        .uri("http://booking-service:8083"))
                .build();
    }
}
