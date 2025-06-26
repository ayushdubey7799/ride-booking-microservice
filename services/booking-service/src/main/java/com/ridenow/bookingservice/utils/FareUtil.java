package com.ridenow.bookingservice.utils;

public class FareUtil {
    public static double calculateFare(double distanceKm) {
        return 50 + (distanceKm * 10); // ₹50 base fare + ₹10 per km
    }
}

