
package com.weathersense.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AirQualityApiClient {

    private final RestClient restClient;
    private final String apiKey;

    public AirQualityApiClient(
            @Value("${weather.api.key}") String apiKey
    ) {
        this.apiKey = apiKey;

        this.restClient = RestClient.builder()
                .baseUrl("https://api.weatherapi.com")
                .build();
    }

    public String getAirQuality(
            double latitude,
            double longitude
    ) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/current.json")
                        .queryParam("key", apiKey)
                        .queryParam(
                                "q",
                                latitude + "," + longitude
                        )
                        .queryParam("aqi", "yes")
                        .build())
                .retrieve()
                .body(String.class);
    }
}
