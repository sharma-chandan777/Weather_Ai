
package com.weathersense.client;

import com.weathersense.service.ApiRequestStats;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class WeatherApiClient {

    private final RestClient restClient;
    private final ApiRequestStats apiRequestStats;
    private final String apiKey;

    public WeatherApiClient(
            ApiRequestStats apiRequestStats,
            @Value("${weather.api.key}") String apiKey
    ) {
        this.apiRequestStats = apiRequestStats;
        this.apiKey = apiKey;

        this.restClient = RestClient.builder()
                .baseUrl("https://api.weatherapi.com")
                .build();
    }

    public String getWeather(double latitude, double longitude) {

        System.out.println("Fetching fresh weather from WeatherAPI");
        System.out.println("Location: " + latitude + ", " + longitude);

        apiRequestStats.recordRequest();

        try {
            String response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/forecast.json")
                            .queryParam("key", apiKey)
                            .queryParam("q", latitude + "," + longitude)
                            .queryParam("days", 3)
                            .queryParam("aqi", "no")
                            .queryParam("alerts", "no")
                            .build())
                    .retrieve()
                    .body(String.class);

            if (response == null || response.isBlank()) {
                throw new IllegalStateException(
                        "Empty response received from WeatherAPI"
                );
            }

            apiRequestStats.recordSuccess();

            System.out.println("WeatherAPI request successful");
            apiRequestStats.printStats();

            return response;

        } catch (HttpClientErrorException.TooManyRequests e) {

            apiRequestStats.recordRateLimit();
            apiRequestStats.recordFailure();

            System.err.println("WEATHER API RATE LIMIT EXCEEDED!");
            System.err.println(e.getResponseBodyAsString());

            apiRequestStats.printStats();
            throw e;

        } catch (Exception e) {

            apiRequestStats.recordFailure();

            System.err.println("WEATHERAPI REQUEST FAILED!");
            e.printStackTrace();

            apiRequestStats.printStats();

            throw new RuntimeException(
                    "WeatherAPI failed: " + e.getMessage(), e
            );
        }
    }
}
