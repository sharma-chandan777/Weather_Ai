function Forecast({ forecastData = [] }) {
  const safeList = Array.isArray(forecastData) ? forecastData : [];

  return (
    <section className="weather-card forecast-card">
      <div className="card-title">
        <h3>3-Day Forecast</h3>
        <span>Next days</span>
      </div>

      {/* Fallback agar data abhi load nahi hua hai */}
      {safeList.length === 0 ? (
        <div style={{ textAlign: "center", padding: "1rem" }}>
          <small>Loading forecast...</small>
        </div>
      ) : (
        <div className="forecast-list">
          {safeList.map((item, index) => (
            <div className="forecast-item" key={item?.day || index}>
              <strong>{item?.day || "N/A"}</strong>

              <span className="forecast-icon">
                {item?.icon || "☀️"}
              </span>

              {/* Fixed: tempMax/tempMin mapping matched with weatherApi */}
              <div>
                <strong className="block">{item?.tempMax ?? "--"}°</strong>
                <small className="opacity-75">{item?.tempMin ?? "--"}°</small>
              </div>

              {/* Fixed: pop (probability of precipitation) mapped safely */}
              <small>💧 {item?.pop ?? 0}%</small>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}

export default Forecast;