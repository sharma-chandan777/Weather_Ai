package com.weathersense.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AirQualityApiClient {

    private final RestClient restClient;

    public AirQualityApiClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://air-quality-api.open-meteo.com")
                .build();
    }

    public String getAirQuality(
            double latitude,
            double longitude
    ) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/air-quality")
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam(
                                "current",
                                "european_aqi," +
                                        "pm2_5," +
                                        "pm10," +
                                        "carbon_monoxide"
                        )
                        .queryParam("timezone", "auto")
                        .build())
                .retrieve()
                .body(String.class);
    }
}