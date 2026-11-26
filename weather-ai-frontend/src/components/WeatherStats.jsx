function WeatherStats({ humidity, windSpeed, pressure, uvIndex, uvLevel }) {
  return (
    <section className="weather-card stats-card">
      <div className="card-title">
        <h3>Weather Details</h3>
        <span>Today</span>
      </div>

      <div className="stats-grid">
        <div className="stat">
          <span>💧</span>
          <small>Humidity</small>
          <strong>{humidity != null ? `${humidity}%` : 'N/A'}</strong>
        </div>

        <div className="stat">
          <span>💨</span>
          <small>Wind</small>
          <strong>{windSpeed != null ? `${windSpeed} km/h` : 'N/A'}</strong>
        </div>

        <div className="stat">
          <span>🌡️</span>
          <small>Pressure</small>
          <strong>{pressure != null ? `${pressure} hPa` : 'N/A'}</strong>
        </div>

        <div className="stat">
          <span>☀️</span>
          <small>UV Index</small>
          <strong>
            {uvIndex != null 
              ? `${uvIndex}${uvLevel ? ` (${uvLevel})` : ''}` 
              : 'N/A'}
          </strong>
        </div>
      </div>
    </section>
  );
}

export default WeatherStats;