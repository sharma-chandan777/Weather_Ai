
package com.weathersense.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weathersense.client.AirQualityApiClient;
import com.weathersense.client.GeocodingApiClient;
import com.weathersense.dto.AirQualityResponse;
import org.springframework.stereotype.Service;

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
                    .readTree(geocodingApiClient.searchCity(city));

            if (!results.isArray() || results.isEmpty()) {
                throw new IllegalArgumentException(
                        "City not found: " + city
                );
            }

            JsonNode location = results.get(0);

            double latitude = location.path("lat").asDouble();
            double longitude = location.path("lon").asDouble();

            String actualCity = location.path("name").asText(city);

            String response = airQualityApiClient.getAirQuality(
                    latitude, longitude
            );

            JsonNode root = objectMapper.readTree(response);

            JsonNode current = root.path("current");
            JsonNode airQuality = current.path("air_quality");

            if (!airQuality.isObject()) {
                throw new IllegalStateException(
                        "Air quality data missing from WeatherAPI"
                );
            }

            // WeatherAPI US EPA AQI
            double aqi = airQuality.path(
                    "us-epa-index"
            ).asDouble(0.0);

            double pm25 = airQuality.path(
                    "pm2_5"
            ).asDouble(0.0);

            double pm10 = airQuality.path(
                    "pm10"
            ).asDouble(0.0);

            double co = airQuality.path(
                    "co"
            ).asDouble(0.0);

            String status = getAQIStatus(aqi);
            String description = getAQIDescription(status);

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

            System.err.println("===== AIR QUALITY ERROR =====");
            e.printStackTrace();
            System.err.println("=============================");

            throw new RuntimeException(
                    "Unable to fetch air quality for " + city, e
            );
        }
    }

    private String getAQIStatus(double aqi) {

        // US EPA AQI scale: 1 to 6

        if (aqi <= 1) return "Good";
        if (aqi <= 2) return "Moderate";
        if (aqi <= 3) return "Poor";
        if (aqi <= 4) return "Unhealthy";
        if (aqi <= 5) return "Very Unhealthy";

        return "Hazardous";
    }

    private String getAQIDescription(String status) {

        return switch (status) {
            case "Good" ->
                    "Air quality is satisfactory and poses little to no risk.";

            case "Moderate" ->
                    "Acceptable air quality; sensitive individuals may experience slight irritation.";

            case "Poor" ->
                    "Air pollution is elevated; sensitive groups should limit prolonged outdoor activity.";

            case "Unhealthy" ->
                    "Unhealthy air quality; reduce prolonged outdoor exertion.";

            case "Very Unhealthy" ->
                    "Very unhealthy air quality; avoid prolonged outdoor activities.";

            default ->
                    "Hazardous air quality; avoid outdoor exertion and follow local health guidance.";
        };
    }
}
