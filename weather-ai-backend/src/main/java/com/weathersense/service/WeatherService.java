package com.weathersense.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weathersense.client.GeocodingApiClient;
import com.weathersense.client.WeatherApiClient;
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

    private final WeatherApiClient weatherApiClient;
    private final GeocodingApiClient geocodingApiClient;
    private final ObjectMapper objectMapper;

    public WeatherService(
            WeatherApiClient weatherApiClient,
            GeocodingApiClient geocodingApiClient,
            ObjectMapper objectMapper
    ) {
        this.weatherApiClient = weatherApiClient;
        this.geocodingApiClient = geocodingApiClient;
        this.objectMapper = objectMapper;
    }

    public WeatherResponse getCurrentWeather(String city) {
        try {
            JsonNode location = findLocation(city);

            double latitude = location.get("latitude").asDouble();
            double longitude = location.get("longitude").asDouble();
            String actualCity = location.get("name").asText();

            String response = weatherApiClient.getWeather(latitude, longitude);

            JsonNode root = objectMapper.readTree(response);
            JsonNode current = root.get("current");

            int weatherCode = current.has("weather_code") ? current.get("weather_code").asInt() : 0;
            int isDay = current.path("is_day").asInt(-1);
            String condition = weatherCondition(weatherCode);
            String icon = weatherIcon(weatherCode);

            double temp = current.has("temperature_2m") ? current.get("temperature_2m").asDouble() : 0.0;
            double feelsLike = current.has("apparent_temperature") ? current.get("apparent_temperature").asDouble() : temp;
            int humidity = current.has("relative_humidity_2m") ? current.get("relative_humidity_2m").asInt() : 0;
            double windSpeed = current.has("wind_speed_10m") ? current.get("wind_speed_10m").asDouble() : 0.0;

            // Visibility in km
            double visibility = current.has("visibility") ? current.get("visibility").asDouble() / 1000.0 : 10.0;

            // Pressure and UV Index directly from live API if present
            double pressure = current.has("surface_pressure") ? current.get("surface_pressure").asDouble() : 1013.25;
            double uvIndex = current.has("uv_index") ? current.get("uv_index").asDouble() : 0.0;
            String uvLevel = getUvLevel(uvIndex);

            // DYNAMIC Agriculture Insights (Calculated based on Live Temp, Humidity & Weather Condition)
            String agriTitle = "Crop & Soil Insight";
            String agriStatusLevel = getAgriStatusLevel(temp, humidity, weatherCode);
            String agriStatusText = getAgriStatusText(agriStatusLevel);
            String agriDescription = generateDynamicAgriAdvice(temp, humidity, weatherCode, actualCity);

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
            System.err.println("===== WEATHER API ERROR =====");
            e.printStackTrace();
            System.err.println("==============================");

            throw new RuntimeException(
                    "Unable to fetch weather for " + city, e
            );
        }
    }

    public ForecastResponse getForecast(String city) {
        try {
            JsonNode location = findLocation(city);

            double latitude = location.get("latitude").asDouble();
            double longitude = location.get("longitude").asDouble();
            String actualCity = location.get("name").asText();

            String response = weatherApiClient.getWeather(latitude, longitude);

            JsonNode daily = objectMapper.readTree(response).get("daily");

            List<ForecastDay> forecast = new ArrayList<>();

            JsonNode dates = daily.get("time");
            JsonNode maxTemps = daily.get("temperature_2m_max");
            JsonNode minTemps = daily.get("temperature_2m_min");
            JsonNode rainProbability = daily.has("precipitation_probability_max") ? daily.get("precipitation_probability_max") : null;
            JsonNode weatherCodes = daily.get("weather_code");

            for (int i = 0; i < dates.size(); i++) {
                String dateStr = dates.get(i).asText();
                String dayLabel = formatDayLabel(dateStr, i);

                int code = weatherCodes.get(i).asInt();
                int precipProb = (rainProbability != null && rainProbability.has(i)) ? rainProbability.get(i).asInt() : 0;

                forecast.add(
                        new ForecastDay(
                                dateStr,
                                dayLabel,
                                maxTemps.get(i).asDouble(),
                                minTemps.get(i).asDouble(),
                                precipProb,
                                code,
                                weatherCondition(code),
                                weatherIcon(code)
                        )
                );
            }

            return new ForecastResponse(actualCity, forecast);

        } catch (Exception e) {
            throw new RuntimeException("Unable to fetch forecast for " + city, e);
        }
    }

    private JsonNode findLocation(String city) throws Exception {
        String response = geocodingApiClient.searchCity(city);
        JsonNode results = objectMapper.readTree(response).get("results");

        if (results == null || results.isEmpty()) {
            throw new IllegalArgumentException("City not found: " + city);
        }

        return results.get(0);
    }

    private String formatDayLabel(String dateStr, int index) {
        if (index == 0) return "Today";
        if (index == 1) return "Tomorrow";
        try {
            LocalDate date = LocalDate.parse(dateStr);
            return date.format(DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH));
        } catch (Exception e) {
            return dateStr;
        }
    }

    // Dynamic Agricultural Calculations
    private String getAgriStatusLevel(double temp, int humidity, int weatherCode) {
        if (weatherCode >= 51 || temp > 38 || temp < 5) {
            return "Warning";
        } else if (temp > 32 || humidity > 85) {
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

    private String generateDynamicAgriAdvice(double temp, int humidity, int weatherCode, String city) {
        if (weatherCode >= 51) {
            return String.format("Rain/Precipitation detected in %s. Delay fertilizer application and pesticide spraying to prevent runoff.", city);
        } else if (temp > 35) {
            return String.format("High temperature (%.1f°C) detected. Ensure proper soil moisture and schedule irrigation during early morning hours.", temp);
        } else if (humidity > 80) {
            return String.format("High atmospheric humidity (%d%%) increases fungal risk. Monitor leaf surface moisture closely.", humidity);
        } else {
            return String.format("Current weather in %s (%.1f°C, %d%% humidity) is ideal for standard agricultural operations and crop maintenance.", city, temp, humidity);
        }
    }

    private String getUvLevel(double uvIndex) {
        if (uvIndex <= 2) return "Low";
        if (uvIndex <= 5) return "Moderate";
        if (uvIndex <= 7) return "High";
        if (uvIndex <= 10) return "Very High";
        return "Extreme";
    }

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
            default -> "Clear";
        };
    }

    private String weatherIcon(int code) {
        return switch (code) {
            case 0 -> "☀️";
            case 1, 2, 3 -> "⛅";
            case 45, 48 -> "🌫️";
            case 51, 53, 55, 56, 57, 61, 63, 65, 80, 81, 82 -> "🌧️";
            case 71, 73, 75, 77, 85, 86 -> "❄️";
            case 95, 96, 99 -> "⛈️";
            default -> "🌤️";
        };
    }
}