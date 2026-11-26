package com.weathersense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WeatherResponse(
        String city,

        @JsonProperty("temp") // React expects 'temp'
        double temperature,

        double feelsLike,
        int humidity,
        double windSpeed,
        double visibility,
        double pressure,       // WeatherStats ke liye
        double uvIndex,        // WeatherStats ke liye
        String uvLevel,        // e.g., "Moderate", "High"
        int weatherCode,
        String condition,
        String icon,           // Weather emoji / icon string

        // Agriculture Card insights
        String agriTitle,
        String agriDescription,
        String agriStatusLevel, // e.g., "Good", "Warning"
        String agriStatusText   // e.g., "Favorable for Irrigation"
) {
}