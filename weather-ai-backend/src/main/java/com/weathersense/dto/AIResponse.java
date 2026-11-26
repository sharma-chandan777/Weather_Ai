package com.weathersense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AIResponse(
        @JsonProperty("reply")
        String answer
) {
}