
package com.weathersense.service;

import com.weathersense.dto.AIRequest;
import com.weathersense.dto.AIResponse;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AIService {

    private final ChatClient chatClient;

    public AIService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public AIResponse ask(AIRequest request) {

        String prompt = """
                You are WeatherSense AI,
                an agricultural and weather assistant.

                City: %s

                User Question: %s

                Give a helpful, clear and practical answer.
                Do not invent live weather information.
                If live weather data is unavailable,
                clearly tell the user.
                """.formatted(
                request.city(),
                request.prompt()
        );

        String replyText = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return new AIResponse(replyText);
    }
}
