
package com.weathersense.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GeocodingApiClient {

    private final RestClient restClient;
    private final String apiKey;

    public GeocodingApiClient(
            @Value("${weather.api.key}") String apiKey
    ) {
        this.apiKey = apiKey;

        this.restClient = RestClient.builder()
                .baseUrl("https://api.weatherapi.com")
                .build();
    }

    public String searchCity(String city) {

        System.out.println("Searching city using WeatherAPI: " + city);

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/search.json")
                        .queryParam("key", apiKey)
                        .queryParam("q", city)
                        .build())
                .retrieve()
                .body(String.class);
    }
}
