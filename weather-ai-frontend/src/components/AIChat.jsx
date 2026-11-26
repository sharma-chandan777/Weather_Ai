
import { useState, useRef, useEffect } from "react";

const AI_BASE_URL =
    "https://weather-ai-backend-uaur.onrender.com/api/ai";

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

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({
      behavior: "smooth"
    });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, isLoading]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!question.trim() || isLoading) return;

    const userQuery = question.trim();

    setMessages((prev) => [
      ...prev,
      { role: "user", text: userQuery }
    ]);

    setQuestion("");
    setIsLoading(true);

    try {
      const response = await fetch(`${AI_BASE_URL}/ask`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          prompt: userQuery,
          city: currentCity
        })
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(
            data.message || `Server error: ${response.status}`
        );
      }

      setMessages((prev) => [
        ...prev,
        {
          role: "ai",
          text: data.reply || "No response received from AI."
        }
      ]);

    } catch (error) {
      console.error("AI Error:", error);

      setMessages((prev) => [
        ...prev,
        {
          role: "ai",
          text: "⚠️ Unable to connect to WeatherSense AI. Please try again."
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

          {isLoading && (
              <div className="message ai loading">
                <div className="avatar">✨</div>
                <div className="message-content">
                  <span>WeatherSense AI is thinking...</span>
                </div>
              </div>
          )}

          <div ref={messagesEndRef} />
        </div>

        <form className="chat-input" onSubmit={handleSubmit}>
          <input
              type="text"
              placeholder="Ask AI about weather or farming..."
              value={question}
              disabled={isLoading}
              onChange={(e) => setQuestion(e.target.value)}
          />

          <button
              type="submit"
              disabled={isLoading || !question.trim()}
          >
            {isLoading ? "..." : "→"}
          </button>
        </form>
      </div>
  );
}

export default AIChat;
