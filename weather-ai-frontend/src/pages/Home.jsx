import SearchBar from "../components/SearchBar";

import CurrentWeather from "../components/CurrentWeather";

import WeatherStats from "../components/WeatherStats";

import Forecast from "../components/Forecast";

import AirQuality from "../components/AirQuality";

import AgricultureCard from "../components/AgricultureCard";



function Home({

  city,

  onSearch,

  weatherData,

  forecastData,

  airQualityData,

  loading,

  error,

  setActivePage

}) {

  return (

    <main className="container">

      <section className="hero">

        <div>

          <p className="eyebrow">WEATHER & ATMOSPHERE</p>

          <h1>

            Understand your weather.

            <span> Make better decisions.</span>

          </h1>

          <p className="hero-text">

            Real-time weather, air quality, agriculture insights and an AI assistant — all in one place.

          </p>

        </div>



        <SearchBar onSearch={onSearch} isLoading={loading} />

      </section>



      {/* Loading or Error states */}

      {loading && <div className="status-message">Loading weather details...</div>}

      {error && <div className="status-message error">{error}</div>}



      {!loading && !error && (

        <section className="dashboard-grid">

          <CurrentWeather

            city={city}

            condition={weatherData?.condition}

            icon={weatherData?.icon}

            temp={weatherData?.temp}

            feelsLike={weatherData?.feelsLike}

            humidity={weatherData?.humidity}

            windSpeed={weatherData?.windSpeed}

            visibility={weatherData?.visibility}

          />



          <WeatherStats

            humidity={weatherData?.humidity}

            windSpeed={weatherData?.windSpeed}

            pressure={weatherData?.pressure}

            uvIndex={weatherData?.uvIndex}

            uvLevel={weatherData?.uvLevel}

          />



          <Forecast forecastData={forecastData} />



          <AirQuality

            aqi={airQualityData?.aqi}

            status={airQualityData?.status}

            description={airQualityData?.description}

            pm25={airQualityData?.pm25}

            pm10={airQualityData?.pm10}

            co={airQualityData?.co}

          />



          <AgricultureCard

            title={weatherData?.agriTitle}

            description={weatherData?.agriDescription}

            statusLevel={weatherData?.agriStatusLevel}

            statusText={weatherData?.agriStatusText}

            onAskAI={() => setActivePage("assistant")}

          />

        </section>

      )}

    </main>

  );

}



export default Home; 

