import { mapWmoToCondition, getAqiStatus } from "../utils/weatherUtils";

const BASE_URL =
    "https://weather-ai-backend-uaur.onrender.com/api/weather";

export async function getWeatherByCity(city = "Shimla") {
  try {
    // 1. Fetch Current Weather
    const currRes = await fetch(`${BASE_URL}/current?city=${encodeURIComponent(city)}`);
    const currData = currRes.ok ? await currRes.json() : {};

    const wCode = currData?.weatherCode ?? currData?.weathercode ?? 0;
    const conditionInfo = mapWmoToCondition(wCode);

    const weatherData = {
      temp: currData?.temp ?? currData?.temperature ?? 25,
      condition: conditionInfo?.label || "Clear",
      weatherCode: wCode,
      icon: conditionInfo?.icon || "☀️",
      feelsLike: currData?.feelsLike ?? currData?.apparent_temperature ?? 25,
      humidity: currData?.humidity ?? currData?.relative_humidity ?? 45,
      windSpeed: currData?.windSpeed ?? currData?.windspeed ?? 10,
      visibility: "10",
      pressure: "1013",
      uvIndex: "5",
      uvLevel: "Moderate",
      agriTitle: "Farming Conditions",
      agriDescription: "Optimal weather conditions for field work.",
      agriStatusLevel: "optimal",
      agriStatusText: "GOOD"
    };

    // 2. Fetch Forecast (Agar backend error de ya empty ho, toh safe fallback use karega)
    let forecastData = [];
    try {
      const foreRes = await fetch(`${BASE_URL}/forecast?city=${encodeURIComponent(city)}`);
      if (foreRes.ok) {
        const foreData = await foreRes.json();
        const list = Array.isArray(foreData) ? foreData : (foreData?.daily || foreData?.forecastList || []);
        
        forecastData = list.slice(0, 5).map((item, index) => ({
          day: item?.day || (item?.time ? new Date(item.time).toLocaleDateString("en-US", { weekday: "short" }) : `Day ${index + 1}`),
          tempMax: item?.tempMax ?? item?.temperature_2m_max ?? 28,
          tempMin: item?.tempMin ?? item?.temperature_2m_min ?? 18,
          icon: mapWmoToCondition(item?.weatherCode ?? 0)?.icon || "☀️",
          pop: item?.pop ?? 10
        }));
      }
    } catch (e) {
      console.warn("Forecast endpoint failed, using fallback data");
    }

    // Fallback agar forecast empty aaye
    if (forecastData.length === 0) {
      forecastData = [
        { day: "Today", tempMax: 26, tempMin: 18, icon: "☀️", pop: 10 },
        { day: "Tue", tempMax: 24, tempMin: 16, icon: "⛅", pop: 20 },
        { day: "Wed", tempMax: 22, tempMin: 15, icon: "🌧️", pop: 60 },
        { day: "Thu", tempMax: 25, tempMin: 17, icon: "☀️", pop: 5 },
        { day: "Fri", tempMax: 27, tempMin: 19, icon: "☀️", pop: 0 },
      ];
    }

    const airQualityData = {
      aqi: 45,
      status: "Good",
      description: "Air quality is satisfactory.",
      pm25: "22",
      pm10: "45",
      co: "0.4"
    };

    return { weatherData, forecastData, airQualityData };
  } catch (error) {
    console.error("API Error in getWeatherByCity:", error);
    throw error;
  }
}

export const getCurrentWeather = async (city) => (await getWeatherByCity(city)).weatherData;
export const getForecast = async (city) => (await getWeatherByCity(city)).forecastData;
export const getAirQuality = async (city) => (await getWeatherByCity(city)).airQualityData;