import { mapWmoToCondition, getAqiStatus } from "../utils/weatherUtils";

const BASE_URL =
    "https://weather-ai-backend-uaur.onrender.com/api/weather";

export async function getWeatherByCity(city = "Shimla") {
  try {
    // 1. Fetch Current Weather

    const currRes = await fetch(
        `${BASE_URL}/current?city=${encodeURIComponent(city)}`
    );

    if (!currRes.ok) {
      throw new Error(`Weather API failed: ${currRes.status}`);
    }

    const currData = await currRes.json();

// Missing temperature ko fake 25°C se replace nahi karna
    const temp = currData?.temp ?? currData?.temperature;

    if (temp == null || !Number.isFinite(Number(temp))) {
      throw new Error("Valid temperature missing from backend response");
    }

    const wCode = currData?.weatherCode ?? currData?.weathercode;

    const conditionInfo = mapWmoToCondition(wCode);

    const weatherData = {
      temp: Number(temp),
      condition: currData?.condition ?? conditionInfo.label,
      weatherCode: wCode,
      icon: currData?.icon ?? conditionInfo.icon,
      feelsLike: currData?.feelsLike ?? currData?.apparent_temperature ?? null,
      humidity: currData?.humidity ?? currData?.relative_humidity_2m ?? null,
      windSpeed: currData?.windSpeed ?? currData?.wind_speed_10m ?? null,
      visibility: currData?.visibility ?? null,
      isDay: currData?.isDay ?? currData?.is_day ?? null,
      pressure: currData?.pressure ?? null,
      uvIndex: currData?.uvIndex ?? null,
      uvLevel: currData?.uvLevel ?? null,
      agriTitle: currData?.agriTitle,
      agriDescription: currData?.agriDescription,
      agriStatusLevel: currData?.agriStatusLevel,
      agriStatusText: currData?.agriStatusText
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