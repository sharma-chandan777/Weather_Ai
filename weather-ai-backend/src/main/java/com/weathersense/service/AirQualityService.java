package com.weathersense.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.weathersense.client.AirQualityApiClient;
import com.weathersense.client.GeocodingApiClient;
import com.weathersense.dto.AirQualityResponse;
import org.springframework.stereotype.Service;
// ✅ SAHI IMPORT (Yeh Paste Karein)

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AirQualityService {

    private final AirQualityApiClient airQualityApiClient;
    private final GeocodingApiClient geocodingApiClient;
    private final ObjectMapper objectMapper;

    public AirQualityService(
            AirQualityApiClient airQualityApiClient,
            GeocodingApiClient geocodingApiClient,
            ObjectMapper objectMapper
    ) {
        this.airQualityApiClient = airQualityApiClient;
        this.geocodingApiClient = geocodingApiClient;
        this.objectMapper = objectMapper;
    }

    public AirQualityResponse getAirQuality(String city) {

        try {
            JsonNode results = objectMapper
                    .readTree(geocodingApiClient.searchCity(city))
                    .get("results");

            if (results == null || results.isEmpty()) {
                throw new IllegalArgumentException("City not found: " + city);
            }

            JsonNode location = results.get(0);
            double latitude = location.get("latitude").asDouble();
            double longitude = location.get("longitude").asDouble();
            String actualCity = location.get("name").asText();

            String response = airQualityApiClient.getAirQuality(latitude, longitude);
            JsonNode current = objectMapper.readTree(response).get("current");

            // Open-Meteo European AQI value (0 - 100+ scale)
            double aqi = current.has("european_aqi") ? current.get("european_aqi").asDouble() : 0.0;
            double pm25 = current.has("pm2_5") ? current.get("pm2_5").asDouble() : 0.0;
            double pm10 = current.has("pm10") ? current.get("pm10").asDouble() : 0.0;
            double co = current.has("carbon_monoxide") ? current.get("carbon_monoxide").asDouble() : 0.0;

            // Compute status and description for React UI
            String status = getAQIStatus(aqi);
            String description = getAQIDescription(status);

            // DTO signature: (city, aqi, status, description, pm25, pm10, co)
            return new AirQualityResponse(
                    actualCity,
                    aqi,
                    status,
                    description,
                    pm25,
                    pm10,
                    co
            );

        } catch (Exception e) {
            throw new RuntimeException("Unable to fetch air quality for " + city, e);
        }
    }

    private String getAQIStatus(double aqi) {
        if (aqi <= 20) return "Good";
        if (aqi <= 40) return "Fair";
        if (aqi <= 60) return "Moderate";
        if (aqi <= 80) return "Poor";
        if (aqi <= 100) return "Very Poor";
        return "Extremely Poor";
    }

    private String getAQIDescription(String status) {
        return switch (status) {
            case "Good", "Fair" -> "Air quality is satisfactory and poses little to no risk.";
            case "Moderate" -> "Acceptable air quality; sensitive individuals may experience slight irritation.";
            case "Poor" -> "High pollution; outdoor activities should be reduced for sensitive groups.";
            default -> "Unhealthy air quality; avoid prolonged outdoor exertion.";
        };
    }
}