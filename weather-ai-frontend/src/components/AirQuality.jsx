import { getAQIStatus } from "../utils/weatherUtils";

function AirQuality({
  aqi = "--",
  description = "Fetching air quality data...",
  pm25 = "--",
  pm10 = "--",
  co = "--"
}) {
  // Safe numeric conversion for calculation & styling
  const numericAQI = aqi !== "--" && aqi != null ? Number(aqi) : null;

  // Auto-calculate dynamic status & text color class from weatherUtils
  const { status, color } = getAQIStatus(numericAQI);

  // Dynamic progress bar width calculation (Max capped at 100%)
  const barWidth = numericAQI != null ? Math.min((numericAQI / 500) * 100, 100) : 0;

  return (
    <section className="weather-card air-card">
      <div className="card-title">
        <h3>Air Quality</h3>
        <span>Atmosphere</span>
      </div>

      <div className="aqi-main">
        <div className="aqi-number">
          {aqi}
        </div>

        <div>
          {/* Dynamic Tailwind/CSS status color */}
          <strong className={`aqi-status ${color}`}>
            {status}
          </strong>
          <p>{description}</p>
        </div>
      </div>

      {/* Dynamic Progress Bar */}
      <div className="aqi-bar" style={{ backgroundColor: "#e5e7eb", borderRadius: "4px", overflow: "hidden" }}>
        <div 
          className={`bar-fill ${color.replace('text-', 'bg-')}`} 
          style={{ 
            width: `${barWidth}%`, 
            height: "8px", 
            transition: "width 0.5s ease-in-out" 
          }}
        ></div>
      </div>

      <div className="pollutants">
        <div>
          <span>PM2.5</span>
          <strong>{pm25 !== "--" && pm25 != null ? `${pm25} µg/m³` : "--"}</strong>
        </div>

        <div>
          <span>PM10</span>
          <strong>{pm10 !== "--" && pm10 != null ? `${pm10} µg/m³` : "--"}</strong>
        </div>

        <div>
          <span>CO</span>
          <strong>{co !== "--" && co != null ? `${co} µg/m³` : "--"}</strong>
        </div>
      </div>
    </section>
  );
}

export default AirQuality;