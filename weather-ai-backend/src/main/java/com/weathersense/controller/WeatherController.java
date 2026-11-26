package com.weathersense.controller;

import com.weathersense.dto.ForecastResponse;
import com.weathersense.dto.WeatherResponse;
import com.weathersense.service.WeatherService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*") // React frontend request allow karne ke liye
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(
            WeatherService weatherService
    ) {
        this.weatherService = weatherService;
    }

    @GetMapping("/current")
    public WeatherResponse currentWeather(
            @RequestParam String city
    ) {
        return weatherService.getCurrentWeather(city);
    }

    @GetMapping("/forecast")
    public ForecastResponse forecast(
            @RequestParam String city
    ) {
        return weatherService.getForecast(city);
    }
}