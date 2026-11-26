package com.weathersense.controller;

import com.weathersense.dto.AirQualityResponse;
import com.weathersense.service.AirQualityService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather") // Updated to match /api/weather/air-quality
@CrossOrigin(origins = "*")     // Fixes CORS issue with React
public class AirQualityController {

    private final AirQualityService airQualityService;

    public AirQualityController(AirQualityService airQualityService) {
        this.airQualityService = airQualityService;
    }

    @GetMapping("/air-quality")
    public AirQualityResponse getAirQuality(@RequestParam String city) {
        return airQualityService.getAirQuality(city);
    }
}