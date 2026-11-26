function AgricultureCard({
  title = "Should you spray today?",
  description = "Loading agriculture insights...",
  statusLevel = "moderate", // Accepts: "good", "moderate", "poor"
  statusText = "Analyzing conditions...",
  onAskAI // Function to switch tab to AI Assistant
}) {
  
  // Dynamic color logic based on condition level
  const getStatusColor = (level) => {
    switch(level?.toLowerCase()) {
      case 'good': return '#2e7d32'; // Green
      case 'poor': return '#d32f2f'; // Red
      case 'moderate': 
      default: return '#ed6c02'; // Orange
    }
  };

  const statusColor = getStatusColor(statusLevel);

  return (
    <section className="weather-card agriculture-card">
      <div className="agri-icon">
        🌱
      </div>

      <div className="agri-content">
        <p className="muted">
          AGRICULTURE INSIGHT
        </p>

        <h3>
          {title}
        </h3>

        <p>
          {description}
        </p>

        {/* Dynamic status color applied here */}
        <div className="agri-status" style={{ color: statusColor }}>
          <span>●</span>
          {statusText}
        </div>

        <button className="ai-button" onClick={onAskAI}>
          Ask AI about your field →
        </button>
      </div>
    </section>
  );
}

export default AgricultureCard;