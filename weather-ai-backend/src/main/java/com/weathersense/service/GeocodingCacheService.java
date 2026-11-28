
package com.weathersense.service;

import com.weathersense.client.GeocodingApiClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class GeocodingCacheService {

    private final GeocodingApiClient geocodingApiClient;

    public GeocodingCacheService(
            GeocodingApiClient geocodingApiClient
    ) {
        this.geocodingApiClient = geocodingApiClient;
    }

    @Cacheable(
            value = "geocodingCache",
            key = "#city.trim().toLowerCase()"
    )
    public String getCachedLocation(String city) {

        System.out.println("Fetching fresh location from Open-Meteo");

        return geocodingApiClient.searchCity(city);
    }
}
