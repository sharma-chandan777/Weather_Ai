package com.weathersense.controller;

import com.weathersense.dto.AIRequest;
import com.weathersense.dto.AIResponse;
import com.weathersense.service.AIService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*") // React frontend access allow karne ke liye
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/ask")
    public AIResponse ask(
            @Valid @RequestBody AIRequest request
    ) {
        return aiService.ask(request);
    }
}