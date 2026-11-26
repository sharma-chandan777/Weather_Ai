package com.weathersense.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GeocodingApiClient {

    private final RestClient restClient;

    public GeocodingApiClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://geocoding-api.open-meteo.com")
                .build();
    }

    public String searchCity(String city) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/search")
                        .queryParam("name", city)
                        .queryParam("count", 1)
                        .queryParam("language", "en")
                        .queryParam("format", "json")
                        .build())
                .retrieve()
                .body(String.class);
    }
}