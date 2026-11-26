const AI_BASE_URL =
    "https://weather-ai-backend-uaur.onrender.com/api/ai";

export async function askAiAssistant(prompt, city = "Delhi") {
  try {
    const response = await fetch(`${AI_BASE_URL}/ask`, {
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
      throw new Error(
          `AI Backend Error (${response.status}): ${errorText}`
      );
    }

    const data = await response.json();

    return data.reply || data.response ||
        "No response received from AI service.";

  } catch (error) {
    console.error("Error calling AI Service:", error);
    throw error;
  }
}