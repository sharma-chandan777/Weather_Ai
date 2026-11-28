
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
                        "Current weather data missing from WeatherAPI"
                );
            }

            if (!current.hasNonNull("temp_c")) {
                throw new IllegalStateException(
                        "Temperature missing from WeatherAPI"
                );
            }

            int weatherCode = current.path("condition")
                    .path("code").asInt(1000);

            int isDay = current.path("is_day").asInt(1);

            double temp = current.path("temp_c").asDouble();

            double feelsLike = current.path(
                    "feelslike_c"
            ).asDouble(temp);

            int humidity = current.path(
                    "humidity"
            ).asInt(0);

            double windSpeed = current.path(
                    "wind_kph"
            ).asDouble(0.0);

            double visibility = current.path(
                    "vis_km"
            ).asDouble(10.0);

            double pressure = current.path(
                    "pressure_mb"
            ).asDouble(1013.25);

            double uvIndex = current.path(
                    "uv"
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

            JsonNode forecastNode = root.path("forecast");
            JsonNode forecastDays = forecastNode.path("forecastday");

            if (!forecastDays.isArray() || forecastDays.isEmpty()) {
                throw new IllegalStateException(
                        "Daily forecast data missing from WeatherAPI"
                );
            }

            List<ForecastDay> forecast = new ArrayList<>();

            int count = Math.min(3, forecastDays.size());

            for (int i = 0; i < count; i++) {

                JsonNode dayNode = forecastDays.get(i);
                JsonNode dayData = dayNode.path("day");
                JsonNode conditionNode = dayData.path("condition");

                String dateStr = dayNode.path("date").asText();

                double maxTemp = dayData.path(
                        "maxtemp_c"
                ).asDouble();

                double minTemp = dayData.path(
                        "mintemp_c"
                ).asDouble();

                int code = conditionNode.path("code").asInt(1000);

                int rainChance = dayData.path(
                        "daily_chance_of_rain"
                ).asInt(0);

                int snowChance = dayData.path(
                        "daily_chance_of_snow"
                ).asInt(0);

                int precipProb = Math.max(rainChance, snowChance);

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

        // WeatherAPI returns a direct JSON array.
        // Open-Meteo used a "results" array.

        JsonNode results = root.isArray()
                ? root
                : root.path("results");

        if (!results.isArray() || results.isEmpty()) {
            throw new IllegalArgumentException(
                    "City not found: " + city
            );
        }

        JsonNode location = results.get(0);

        if (!location.hasNonNull("lat")
                || !location.hasNonNull("lon")) {

            throw new IllegalStateException(
                    "Invalid location coordinates for: " + city
            );
        }

        return objectMapper.createObjectNode()
                .put("latitude", location.path("lat").asDouble())
                .put("longitude", location.path("lon").asDouble())
                .put("name", location.path("name").asText(city));
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

    private boolean isRainOrBadWeather(int code) {

        return switch (code) {
            case 1063, 1066, 1069, 1072, 1087,
                 1150, 1153, 1168, 1171,
                 1180, 1183, 1186, 1189, 1192, 1195,
                 1198, 1201, 1204, 1207,
                 1240, 1243, 1246, 1249, 1252,
                 1255, 1258, 1261, 1264,
                 1273, 1276, 1279, 1282 -> true;

            default -> false;
        };
    }

    private String getAgriStatusLevel(
            double temp,
            int humidity,
            int weatherCode
    ) {

        if (isRainOrBadWeather(weatherCode)
                || temp > 38 || temp < 5) {
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

        if (isRainOrBadWeather(weatherCode)) {

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
            case 1000 -> "Clear sky";
            case 1003 -> "Partly cloudy";
            case 1006, 1009 -> "Cloudy";
            case 1030, 1135, 1147 -> "Foggy";

            case 1150, 1153, 1168, 1171 ->
                    "Drizzle";

            case 1066, 1069, 1072, 1114, 1117,
                 1210, 1213, 1216, 1219, 1222, 1225,
                 1237, 1255, 1258, 1261, 1264 ->
                    "Snow";

            case 1087, 1273, 1276, 1279, 1282 ->
                    "Thunderstorm";

            case 1063, 1180, 1183, 1186, 1189,
                 1192, 1195, 1198, 1201,
                 1240, 1243, 1246 ->
                    "Rain";

            case 1204, 1207, 1249, 1252 ->
                    "Rain showers";

            default -> "Unknown";
        };
    }


    // ================= WEATHER ICON =================

    private String weatherIcon(int code) {

        return switch (code) {

            case 1000 -> "☀️";

            case 1003 -> "⛅";

            case 1006, 1009 -> "☁️";

            case 1030, 1135, 1147 -> "🌫️";

            case 1066, 1069, 1072, 1114, 1117,
                 1210, 1213, 1216, 1219, 1222, 1225,
                 1237, 1255, 1258, 1261, 1264 -> "❄️";

            case 1087, 1273, 1276, 1279, 1282 -> "⛈️";

            case 1063, 1150, 1153, 1168, 1171,
                 1180, 1183, 1186, 1189, 1192, 1195,
                 1198, 1201, 1204, 1207,
                 1240, 1243, 1246, 1249, 1252 -> "🌧️";

            default -> "🌤️";
        };
    }
}
