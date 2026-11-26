package com.weathersense.dto;

public record AirQualityResponse(
        String city,
        double aqi,
        String status,       // e.g., "Good", "Moderate"
        String description,  // e.g., "Air quality is healthy."
        double pm25,
        double pm10,
        double co            // React prop 'co' se exact match
) {
}