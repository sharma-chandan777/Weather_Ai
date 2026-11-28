
package com.weathersense.service;

import com.weathersense.client.WeatherApiClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class WeatherCacheService {

    private final WeatherApiClient weatherApiClient;

    public WeatherCacheService(WeatherApiClient weatherApiClient) {
        this.weatherApiClient = weatherApiClient;
    }

    @Cacheable(
            value = "weatherCache",
            key = "#latitude + '_' + #longitude"
    )
    public String getCachedWeather(
            double latitude,
            double longitude
    ) {
        System.out.println("Fetching fresh weather from Open-Meteo");

        return weatherApiClient.getWeather(latitude, longitude);
    }
}
