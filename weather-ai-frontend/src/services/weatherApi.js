
import { mapWmoToCondition } from "../utils/weatherUtils";

const BASE_URL =
    "https://weather-ai-backend-uaur.onrender.com/api/weather";

// Common API request handler
async function fetchWeatherApi(endpoint) {
  const url = `${BASE_URL}${endpoint}`;

  try {
    console.log("Requesting:", url);

    const response = await fetch(url);

    const responseText = await response.text();

    console.log("HTTP Status:", response.status);
    console.log("Backend Response:", responseText);

    if (!response.ok) {
      throw new Error(
          `Backend Error ${response.status}: ${
              responseText || response.statusText
          }`
      );
    }

    try {
      return JSON.parse(responseText);
    } catch {
      throw new Error("Invalid JSON response from backend");
    }
  } catch (error) {
    console.error("Weather API Error:", error);
    throw error;
  }
}

// Fetch complete weather data
export async function getWeatherByCity(city = "Shimla") {
  if (typeof city !== "string" || !city.trim()) {
    throw new Error("Please enter a valid city name");
  }

  const encodedCity = encodeURIComponent(city.trim());

  // 1. Fetch Current Weather
  const currData = await fetchWeatherApi(
      `/current?city=${encodedCity}`
  );

  const temp = currData?.temp ?? currData?.temperature;

  if (
      temp == null ||
      !Number.isFinite(Number(temp))
  ) {
    throw new Error(
        "Valid temperature missing from backend response"
    );
  }

  const wCode =
      currData?.weatherCode ??
      currData?.weathercode ??
      0;

  const conditionInfo = mapWmoToCondition(wCode) || {};

  const weatherData = {
    city: currData?.city ?? city.trim(),

    temp: Number(temp),

    condition:
        currData?.condition ??
        conditionInfo.label ??
        "Unknown",

    weatherCode: wCode,

    icon:
        currData?.icon ??
        conditionInfo.icon ??
        "🌤️",

    feelsLike:
        currData?.feelsLike ??
        currData?.apparent_temperature ??
        null,

    humidity:
        currData?.humidity ??
        currData?.relative_humidity_2m ??
        null,

    windSpeed:
        currData?.windSpeed ??
        currData?.wind_speed_10m ??
        null,

    visibility: currData?.visibility ?? null,

    isDay:
        currData?.isDay ??
        currData?.is_day ??
        null,

    pressure: currData?.pressure ?? null,

    uvIndex: currData?.uvIndex ?? null,

    uvLevel: currData?.uvLevel ?? null,

    agriTitle: currData?.agriTitle ?? "",

    agriDescription:
        currData?.agriDescription ?? "",

    agriStatusLevel:
        currData?.agriStatusLevel ?? "",

    agriStatusText:
        currData?.agriStatusText ?? ""
  };

  // 2. Fetch Forecast
  let forecastData = [];

  try {
    const foreData = await fetchWeatherApi(
        `/forecast?city=${encodedCity}`
    );

    // Correct Java ForecastResponse mapping
    const list = Array.isArray(foreData)
        ? foreData
        : (
            foreData?.forecast ??
            foreData?.daily ??
            foreData?.forecastList ??
            []
        );

    if (!Array.isArray(list)) {
      throw new Error("Invalid forecast data format");
    }

    forecastData = list.slice(0, 3).map((item, index) => {
      const code =
          item?.weatherCode ??
          item?.weathercode ??
          0;

      const info = mapWmoToCondition(code) || {};

      return {
        date: item?.date ?? null,

        day:
            item?.day ??
            (
                item?.date
                    ? new Date(item.date).toLocaleDateString(
                        "en-US",
                        { weekday: "short" }
                    )
                    : `Day ${index + 1}`
            ),

        // Correct Java DTO field names
        tempMax:
            item?.maxTemp ??
            item?.tempMax ??
            null,

        tempMin:
            item?.minTemp ??
            item?.tempMin ??
            null,

        icon:
            item?.icon ??
            info.icon ??
            "🌤️",

        pop:
            item?.precipitationProbability ??
            item?.pop ??
            0,

        condition:
            item?.condition ??
            info.label ??
            "Unknown",

        weatherCode: code
      };
    });

    console.log("Forecast Data:", forecastData);

  } catch (error) {
    console.error(
        "Forecast API failed:",
        error.message
    );

    forecastData = [];
  }

  // 3. Air Quality
  // Placeholder: live AQI API is not integrated yet.
  const airQualityData = {
    aqi: null,
    status: "Unavailable",
    description: "Live air quality data is not connected.",
    pm25: null,
    pm10: null,
    co: null
  };

  return {
    weatherData,
    forecastData,
    airQualityData
  };
}

// Individual functions
export const getCurrentWeather = async (city) => {
  const data = await getWeatherByCity(city);
  return data.weatherData;
};

export const getForecast = async (city) => {
  const data = await getWeatherByCity(city);
  return data.forecastData;
};

export const getAirQuality = async (city) => {
  const data = await getWeatherByCity(city);
  return data.airQualityData;
};
