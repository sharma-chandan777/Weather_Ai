const AI_BASE_URL = "https://weather-ai-backend-uaur.onrender.com";

/**
 * AI Chatbot Query Handler
 * Sends prompt and city context to Spring Boot AI endpoint
 */
export async function askAiAssistant(prompt, city = "Delhi") {
  try {
    // Corrected Endpoint URL: /api/ai/chat
    const response = await fetch(`${AI_BASE_URL}/chat`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        prompt: prompt,
        city: city
      }),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`AI Backend Error (${response.status}): ${errorText || "Server error"}`);
    }

    const data = await response.json();
    
    // Expected response formats handling: { reply: "..." } or { response: "..." }
    return data.reply || data.response || "No response received from AI service.";
  } catch (error) {
    console.error("Error calling AI Service:", error);
    throw error;
  }
}