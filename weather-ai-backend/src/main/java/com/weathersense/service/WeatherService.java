
package com.weathersense.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weathersense.dto.ForecastDay;
import com.weathersense.dto.ForecastResponse;
import com.weathersense.dto.WeatherResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class WeatherService {

    private final WeatherCacheService weatherCacheService;
    private final GeocodingCacheService geocodingCacheService;
    private final ObjectMapper objectMapper;

    public WeatherService(
            WeatherCacheService weatherCacheService,
            GeocodingCacheService geocodingCacheService,
            ObjectMapper objectMapper
    ) {
        this.weatherCacheService = weatherCacheService;
        this.geocodingCacheService = geocodingCacheService;
        this.objectMapper = objectMapper;
    }

    // ================= CURRENT WEATHER =================

    public WeatherResponse getCurrentWeather(String city) {

        validateCity(city);

        try {
            JsonNode location = findLocation(city);

            double latitude = location.get("latitude").asDouble();
            double longitude = location.get("longitude").asDouble();
            String actualCity = location.path("name").asText(city);

            String response = weatherCacheService.getCachedWeather(
                    latitude, longitude
            );

            JsonNode root = objectMapper.readTree(response);

            JsonNode current = root.path("current");

            if (!current.isObject()) {
                throw new IllegalStateException(
                        "Current weather data missing from weather API"
                );
            }

            if (!current.hasNonNull("temperature_2m")) {
                throw new IllegalStateException(
                        "Temperature missing from weather API"
                );
            }

            int weatherCode = current.path("weather_code").asInt(0);
            int isDay = current.path("is_day").asInt(-1);

            double temp = current.path("temperature_2m").asDouble();

            double feelsLike = current.path(
                    "apparent_temperature"
            ).asDouble(temp);

            int humidity = current.path(
                    "relative_humidity_2m"
            ).asInt(0);

            double windSpeed = current.path(
                    "wind_speed_10m"
            ).asDouble(0.0);

            double visibility = current.path(
                    "visibility"
            ).asDouble(10000.0) / 1000.0;

            double pressure = current.path(
                    "surface_pressure"
            ).asDouble(1013.25);

            double uvIndex = current.path(
                    "uv_index"
            ).asDouble(0.0);

            String uvLevel = getUvLevel(uvIndex);

            String condition = weatherCondition(weatherCode);
            String icon = weatherIcon(weatherCode);

            String agriTitle = "Crop & Soil Insight";

            String agriStatusLevel = getAgriStatusLevel(
                    temp, humidity, weatherCode
            );

            String agriStatusText = getAgriStatusText(
                    agriStatusLevel
            );

            String agriDescription = generateDynamicAgriAdvice(
                    temp, humidity, weatherCode, actualCity
            );

            return new WeatherResponse(
                    actualCity,
                    temp,
                    feelsLike,
                    humidity,
                    windSpeed,
                    visibility,
                    pressure,
                    uvIndex,
                    uvLevel,
                    weatherCode,
                    isDay,
                    condition,
                    icon,
                    agriTitle,
                    agriDescription,
                    agriStatusLevel,
                    agriStatusText
            );

        } catch (Exception e) {

            System.err.println("===== CURRENT WEATHER ERROR =====");
            e.printStackTrace();
            System.err.println("=================================");

            throw new RuntimeException(
                    "Unable to fetch current weather for " + city
                            + ": " + e.getMessage(), e
            );
        }
    }

    // ================= FORECAST =================

    public ForecastResponse getForecast(String city) {

        validateCity(city);

        try {
            JsonNode location = findLocation(city);

            double latitude = location.get("latitude").asDouble();
            double longitude = location.get("longitude").asDouble();

            String actualCity = location.path("name").asText(city);

            String response = weatherCacheService.getCachedWeather(
                    latitude, longitude
            );

            JsonNode root = objectMapper.readTree(response);
            JsonNode daily = root.path("daily");

            if (!daily.isObject()) {
                throw new IllegalStateException(
                        "Daily forecast data missing from weather API"
                );
            }

            JsonNode dates = daily.path("time");
            JsonNode maxTemps = daily.path("temperature_2m_max");
            JsonNode minTemps = daily.path("temperature_2m_min");
            JsonNode rainProbability =
                    daily.path("precipitation_probability_max");
            JsonNode weatherCodes = daily.path("weather_code");

            if (!dates.isArray()
                    || !maxTemps.isArray()
                    || !minTemps.isArray()) {

                throw new IllegalStateException(
                        "Invalid forecast response: required daily fields missing"
                );
            }

            if (dates.isEmpty()) {
                throw new IllegalStateException(
                        "No forecast dates available"
                );
            }

            List<ForecastDay> forecast = new ArrayList<>();

            int count = Math.min(
                    5,
                    Math.min(
                            dates.size(),
                            Math.min(maxTemps.size(), minTemps.size())
                    )
            );

            for (int i = 0; i < count; i++) {

                String dateStr = dates.get(i).asText();

                double maxTemp = maxTemps.get(i).asDouble();
                double minTemp = minTemps.get(i).asDouble();

                int code = weatherCodes.isArray()
                        && weatherCodes.size() > i
                        ? weatherCodes.get(i).asInt()
                        : 0;

                int precipProb = rainProbability.isArray()
                        && rainProbability.size() > i
                        ? rainProbability.get(i).asInt()
                        : 0;

                String dayLabel = formatDayLabel(dateStr, i);

                forecast.add(
                        new ForecastDay(
                                dateStr,
                                dayLabel,
                                maxTemp,
                                minTemp,
                                precipProb,
                                code,
                                weatherCondition(code),
                                weatherIcon(code)
                        )
                );
            }

            if (forecast.isEmpty()) {
                throw new IllegalStateException(
                        "Forecast list is empty"
                );
            }

            return new ForecastResponse(actualCity, forecast);

        } catch (Exception e) {

            System.err.println("===== FORECAST ERROR =====");
            e.printStackTrace();
            System.err.println("==========================");

            throw new RuntimeException(
                    "Unable to fetch forecast for " + city
                            + ": " + e.getMessage(), e
            );
        }
    }

    // ================= CITY VALIDATION =================

    private void validateCity(String city) {

        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException(
                    "City name cannot be empty"
            );
        }
    }

    // ================= GEOCODING =================

    private JsonNode findLocation(String city) throws Exception {

        String response = geocodingCacheService.getCachedLocation(
                city.trim()
        );

        if (response == null || response.isBlank()) {
            throw new IllegalStateException(
                    "Empty response from geocoding service"
            );
        }

        JsonNode root = objectMapper.readTree(response);

        JsonNode results = root.path("results");

        if (!results.isArray() || results.isEmpty()) {
            throw new IllegalArgumentException(
                    "City not found: " + city
            );
        }

        JsonNode location = results.get(0);

        if (!location.hasNonNull("latitude")
                || !location.hasNonNull("longitude")) {

            throw new IllegalStateException(
                    "Invalid location coordinates for: " + city
            );
        }

        return location;
    }

    // ================= DATE FORMAT =================

    private String formatDayLabel(String dateStr, int index) {

        if (index == 0) return "Today";
        if (index == 1) return "Tomorrow";

        try {
            LocalDate date = LocalDate.parse(dateStr);

            return date.format(
                    DateTimeFormatter.ofPattern(
                            "EEE", Locale.ENGLISH
                    )
            );

        } catch (Exception e) {
            return dateStr;
        }
    }

    // ================= AGRICULTURE INSIGHTS =================

    private String getAgriStatusLevel(
            double temp,
            int humidity,
            int weatherCode
    ) {

        if (weatherCode >= 51 || temp > 38 || temp < 5) {
            return "Warning";
        }

        if (temp > 32 || humidity > 85) {
            return "Caution";
        }

        return "Optimal";
    }

    private String getAgriStatusText(String statusLevel) {

        return switch (statusLevel) {
            case "Warning" -> "Adverse Weather Alert";
            case "Caution" -> "Monitor Field Conditions";
            default -> "Favorable for Irrigation & Fieldwork";
        };
    }

    private String generateDynamicAgriAdvice(
            double temp,
            int humidity,
            int weatherCode,
            String city
    ) {

        if (weatherCode >= 51) {
            return String.format(
                    "Rain/Precipitation detected in %s. Delay fertilizer application and pesticide spraying to prevent runoff.",
                    city
            );

        } else if (temp > 35) {
            return String.format(
                    "High temperature (%.1f°C) detected. Ensure proper soil moisture and schedule irrigation during early morning hours.",
                    temp
            );

        } else if (humidity > 80) {
            return String.format(
                    "High atmospheric humidity (%d%%) increases fungal risk. Monitor leaf surface moisture closely.",
                    humidity
            );

        } else {
            return String.format(
                    "Current weather in %s (%.1f°C, %d%% humidity) is ideal for standard agricultural operations and crop maintenance.",
                    city, temp, humidity
            );
        }
    }

    // ================= UV INDEX =================

    private String getUvLevel(double uvIndex) {

        if (uvIndex <= 2) return "Low";
        if (uvIndex <= 5) return "Moderate";
        if (uvIndex <= 7) return "High";
        if (uvIndex <= 10) return "Very High";

        return "Extreme";
    }

    // ================= WEATHER CONDITION =================

    private String weatherCondition(int code) {

        return switch (code) {
            case 0 -> "Clear sky";
            case 1, 2, 3 -> "Partly cloudy";
            case 45, 48 -> "Foggy";
            case 51, 53, 55, 56, 57 -> "Drizzle";
            case 61, 63, 65, 66, 67 -> "Rain";
            case 71, 73, 75, 77 -> "Snow";
            case 80, 81, 82 -> "Rain showers";
            case 85, 86 -> "Snow showers";
            case 95, 96, 99 -> "Thunderstorm";
            default -> "Unknown";
        };
    }

    // ================= WEATHER ICON =================

    private String weatherIcon(int code) {

        return switch (code) {
            case 0 -> "☀️";
            case 1, 2, 3 -> "⛅";
            case 45, 48 -> "🌫️";
            case 51, 53, 55, 56, 57,
                 61, 63, 65, 66, 67,
                 80, 81, 82 -> "🌧️";
            case 71, 73, 75, 77, 85, 86 -> "❄️";
            case 95, 96, 99 -> "⛈️";
            default -> "🌤️";
        };
    }
}
