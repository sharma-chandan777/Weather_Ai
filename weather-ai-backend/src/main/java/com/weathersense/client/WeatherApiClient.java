
package com.weathersense.client;

import com.weathersense.service.ApiRequestStats;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class WeatherApiClient {

    private final RestClient restClient;
    private final ApiRequestStats apiRequestStats;

    public WeatherApiClient(ApiRequestStats apiRequestStats) {
        this.apiRequestStats = apiRequestStats;

        this.restClient = RestClient.builder()
                .baseUrl("https://api.open-meteo.com")
                .build();
    }

    public String getWeather(
            double latitude,
            double longitude
    ) {

        System.out.println("Fetching fresh weather from Open-Meteo");
        System.out.println("Location: " + latitude + ", " + longitude);

        // Count actual API attempt
        apiRequestStats.recordRequest();

        try {

            String response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/forecast")
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam(
                                    "current",
                                    "temperature_2m," +
                                            "relative_humidity_2m," +
                                            "apparent_temperature," +
                                            "weather_code," +
                                            "is_day," +
                                            "surface_pressure," +
                                            "wind_speed_10m," +
                                            "visibility," +
                                            "uv_index"
                            )
                            .queryParam(
                                    "daily",
                                    "temperature_2m_max," +
                                            "temperature_2m_min," +
                                            "precipitation_probability_max," +
                                            "weather_code"
                            )
                            .queryParam("timezone", "auto")
                            .queryParam("forecast_days", 5)
                            .build())
                    .retrieve()
                    .body(String.class);

            if (response == null || response.isBlank()) {
                throw new IllegalStateException(
                        "Empty response received from Open-Meteo"
                );
            }

            apiRequestStats.recordSuccess();

            System.out.println("Open-Meteo request successful");
            apiRequestStats.printStats();

            return response;

        } catch (HttpClientErrorException.TooManyRequests e) {

            apiRequestStats.recordRateLimit();
            apiRequestStats.recordFailure();

            System.err.println("OPEN-METEO DAILY LIMIT EXCEEDED!");
            System.err.println(e.getResponseBodyAsString());

            apiRequestStats.printStats();

            throw e;

        } catch (Exception e) {

            apiRequestStats.recordFailure();

            System.err.println("OPEN-METEO API REQUEST FAILED!");
            e.printStackTrace();

            apiRequestStats.printStats();

            throw new RuntimeException(
                    "Open-Meteo API failed: " + e.getMessage(), e
            );
        }
    }
}
