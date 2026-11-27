import { getWeatherDetails } from "../utils/weatherUtils";

function CurrentWeather({
                          city = "Select a City",
                          weatherCode,
                          isDay,
                          condition,
                          temp = "--",
                          feelsLike = "--",
                          humidity = "--",
                          windSpeed = "--",
                          visibility = "--"
                        }) {
  // Weather code pass karke dynamic icon, label, aur dynamic background class extract karein
  const { icon, label, bgClass } =
      getWeatherDetails(weatherCode ?? condition);

  const displayIcon =
      Number(isDay) === 0 && Number(weatherCode) === 0
          ? "🌙"
          : icon;

  return (
    // bgClass se weather ke hisab se gradient color change hoga (Rainy par Dark, Sunny par Orange)
    <section className={`weather-card current-weather bg-gradient-to-br ${bgClass} text-white`}>

      <div className="weather-top">
        <div>
          <p className="muted opacity-80">CURRENT WEATHER</p>
          <h2>{city}</h2>
          <p className="weather-condition">
            {label || condition}
          </p>
        </div>

        {/* Dynamic Icon yahan render hoga */}
        <div className="weather-icon text-5xl">
          {displayIcon}
        </div>
      </div>

      <div className="temperature">
        {temp}°
        <span>C</span>
      </div>

      <div className="feels">
        Feels like {feelsLike}°C
      </div>

      <div className="weather-footer">
        <div>
          <span>Humidity</span>
          <strong>{humidity !== "--" && humidity != null ? `${humidity}%` : "--"}</strong>
        </div>

        <div>
          <span>Wind</span>
          <strong>{windSpeed !== "--" && windSpeed != null ? `${windSpeed} km/h` : "--"}</strong>
        </div>

        <div>
          <span>Visibility</span>
          <strong>{visibility !== "--" && visibility != null ? `${visibility} km` : "--"}</strong>
        </div>
      </div>

    </section>
  );
}

export default CurrentWeather;