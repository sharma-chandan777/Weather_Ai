import { useState, useRef, useEffect } from "react";

function AIChat({ currentCity = "Delhi" }) {
  const [question, setQuestion] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef(null);

  const [messages, setMessages] = useState([
    {
      role: "ai",
      text: "Hi! I'm WeatherSense AI. Ask me anything about weather, atmosphere, farming or outdoor conditions."
    }
  ]);

  // Messages add hone par auto-scroll neche karne ke liye
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, isLoading]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!question.trim() || isLoading) return;

    const userQuery = question.trim();
    const userMessage = { role: "user", text: userQuery };

    // User Message immediately render karein
    setMessages((prev) => [...prev, userMessage]);
    setQuestion("");
    setIsLoading(true);

    try {
      // Spring Boot backend API Endpoint
      const response = await fetch("http://localhost:8080/api/ai/ask", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          // Agar JWT Auth lagaya hai to authorization header:
          // "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({
          prompt: userQuery,
          city: currentCity // Weather-aware context
        }),
      });

      if (!response.ok) {
        throw new Error("Failed to get response from AI server.");
      }

      const data = await response.json(); // Backend should return { reply: "..." }

      setMessages((prev) => [
        ...prev,
        {
          role: "ai",
          text: data.reply || "Sorry, I couldn't process that request."
        }
      ]);
    } catch (error) {
      console.error("AI Error:", error);
      setMessages((prev) => [
        ...prev,
        {
          role: "ai",
          text: "⚠️ Server error! Unable to reach WeatherSense AI backend right now."
        }
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="ai-chat">

      <div className="chat-messages">
        {messages.map((message, index) => (
          <div
            key={index}
            className={`message ${message.role}`}
          >
            <div className="avatar">
              {message.role === "ai" ? "✨" : "👤"}
            </div>

            <div className="message-content">
              {message.text}
            </div>
          </div>
        ))}

        {/* Loading Indicator */}
        {isLoading && (
          <div className="message ai loading">
            <div className="avatar">✨</div>
            <div className="message-content">
              <span>WeatherSense AI is thinking...</span>
            </div>
          </div>
        )}

        {/* Scroll anchor */}
        <div ref={messagesEndRef} />
      </div>

      <form className="chat-input" onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="Ask AI: e.g. Is it a good day to water crops in Delhi?"
          value={question}
          disabled={isLoading}
          onChange={(e) => setQuestion(e.target.value)}
        />

        <button type="submit" disabled={isLoading || !question.trim()}>
          {isLoading ? "..." : "→"}
        </button>
      </form>

    </div>
  );
}

export default AIChat;