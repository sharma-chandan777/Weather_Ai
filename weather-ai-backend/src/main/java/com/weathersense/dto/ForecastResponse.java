package com.weathersense.dto;

import java.util.List;

public record ForecastResponse(
        String city,
        List<ForecastDay> forecast
) {
}