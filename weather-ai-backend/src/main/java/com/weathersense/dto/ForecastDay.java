package com.weathersense.dto;

public record ForecastDay(
        String date,            // e.g., "2026-09-01"
        String day,             // e.g., "Tue" ya "Tomorrow"
        double maxTemp,         // React expects 'maxTemp'
        double minTemp,         // React expects 'minTemp'
        int precipitationProbability,
        int weatherCode,
        String condition,       // e.g., "Sunny", "Rainy"
        String icon             // e.g., "☀️", "🌧️"
) {
}