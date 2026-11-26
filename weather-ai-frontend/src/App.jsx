

import { useState, useEffect } from "react";
import Navbar from "./components/Navbar";
import Home from "./pages/Home";
import Assistant from "./pages/Assistant";
// Single optimized function import karein
import { getWeatherByCity } from "./services/weatherApi";
import "./index.css";

function App() {
  const [activePage, setActivePage] = useState("home");
  const [city, setCity] = useState("Shimla");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [weatherData, setWeatherData] = useState(null);
  const [forecastData, setForecastData] = useState([]);
  const [airQualityData, setAirQualityData] = useState(null);

  const fetchAllData = async (searchCity) => {
    setLoading(true);
    setError(null);
    try {
      // Ek single request se pura consolidated object lein
      const data = await getWeatherByCity(searchCity);

      setWeatherData(data.weatherData);
      setForecastData(data.forecastData);
      setAirQualityData(data.airQualityData);
      setCity(searchCity);
    } catch (err) {
      console.error("Failed to load weather data:", err);
      setError("Spring Boot backend server internal error (500) or city not found!");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAllData(city);
  }, []);

  return (
    <div className="app">
      <Navbar activePage={activePage} setActivePage={setActivePage} />

      {activePage === "home" ? (
        <Home 
          city={city} 
          onSearch={fetchAllData}
          weatherData={weatherData}
          forecastData={forecastData}
          airQualityData={airQualityData}
          loading={loading}
          error={error}
          setActivePage={setActivePage}
        />
      ) : (
        <Assistant currentCity={city} />
      )}
    </div>
  );
}

export default App;