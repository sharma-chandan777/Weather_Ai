package com.weathersense.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WeatherApiClient {

    private final RestClient restClient;

    public WeatherApiClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.open-meteo.com")
                .build();
    }

    public String getWeather(
            double latitude,
            double longitude
    ) {

        return restClient.get()
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
    }
}