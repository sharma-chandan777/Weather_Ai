package com.weathersense.dto;

import jakarta.validation.constraints.NotBlank;

public record AIRequest(

        @NotBlank(message = "Prompt is required")
        String prompt,

        @NotBlank(message = "City is required")
        String city
) {
}