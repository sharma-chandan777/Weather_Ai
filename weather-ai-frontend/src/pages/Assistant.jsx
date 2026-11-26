import { useState } from "react";
import AIChat from "../components/AIChat";

function Assistant({ currentCity = "Delhi" }) {
  const [selectedPrompt, setSelectedPrompt] = useState("");

  const suggestedPrompts = [
    "🌾 Best time for crop spraying today?",
    "🏃 Is it safe for outdoor running with current AQI?",
    "🌧️ Will it rain in the next 24 hours?",
    "👕 What should I wear for today's weather?"
  ];

  const handlePromptClick = (promptText) => {
    // Unicode-safe emoji removal at start of string
    const cleanPrompt = promptText.replace(/^[\p{Extended_Pictographic}\s]+/u, "").trim();
    setSelectedPrompt(cleanPrompt || promptText);
  };

  return (
    <main className="container assistant-page">
      <div className="assistant-header">
        <p className="eyebrow">AI WEATHER ASSISTANT</p>

        <h1>
          Ask anything about
          <span> weather & atmosphere.</span>
        </h1>

        <p>
          Get instant smart insights for farming, outdoor activities, travel, and environmental conditions in <strong>{currentCity}</strong>.
        </p>

        {/* Quick prompt suggestions UI */}
        <div className="suggested-prompts">
          {suggestedPrompts.map((prompt, index) => (
            <span 
              key={index} 
              className="prompt-chip"
              onClick={() => handlePromptClick(prompt)}
            >
              {prompt}
            </span>
          ))}
        </div>
      </div>

      {/* AIChat Component */}
      <AIChat 
        currentCity={currentCity} 
        initialQuestion={selectedPrompt}
        onQuestionConsumed={() => setSelectedPrompt("")}
      />
    </main>
  );
}

export default Assistant;