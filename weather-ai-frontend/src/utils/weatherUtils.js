// 1. WMO Weather Code Mapping (Open-Meteo standard 0-99)
export const mapWmoToCondition = (code) => {
  const wmoMap = {
    0: { label: "Clear Sky", icon: "☀️", bgClass: "from-amber-400 to-orange-500" },
    1: { label: "Mainly Clear", icon: "🌤️", bgClass: "from-amber-300 to-sky-400" },
    2: { label: "Partly Cloudy", icon: "⛅", bgClass: "from-sky-400 to-indigo-400" },
    3: { label: "Overcast", icon: "☁️", bgClass: "from-slate-400 to-gray-600" },
    45: { label: "Foggy", icon: "🌫️", bgClass: "from-gray-400 to-slate-500" },
    48: { label: "Rime Fog", icon: "🌫️", bgClass: "from-gray-400 to-slate-500" },
    51: { label: "Light Drizzle", icon: "🌦️", bgClass: "from-blue-400 to-slate-600" },
    61: { label: "Slight Rain", icon: "🌧️", bgClass: "from-blue-500 to-slate-700" },
    63: { label: "Moderate Rain", icon: "🌧️", bgClass: "from-blue-600 to-slate-800" },
    65: { label: "Heavy Rain", icon: "🌧️", bgClass: "from-indigo-700 to-slate-900" },
    71: { label: "Light Snow", icon: "🌨️", bgClass: "from-blue-200 to-indigo-300" },
    95: { label: "Thunderstorm", icon: "⛈️", bgClass: "from-purple-700 to-slate-900" },
  };

  return wmoMap[code] || { label: "Clear", icon: "☀️", bgClass: "from-blue-400 to-indigo-600" };
};

// Component level helper alias
export const getWeatherDetails = (code) => mapWmoToCondition(code);

// 2. Air Quality Index (AQI) Status Mapping
export const getAqiStatus = (aqi) => {
  if (aqi == null || isNaN(aqi)) {
    return { status: "Unknown", color: "text-gray-500", description: "AQI data unavailable." };
  }
  if (aqi <= 50) {
    return { status: "Good", color: "text-emerald-500", description: "Air quality is satisfactory and poses little risk." };
  }
  if (aqi <= 100) {
    return { status: "Moderate", color: "text-amber-500", description: "Acceptable air quality for most individuals." };
  }
  if (aqi <= 150) {
    return { status: "Unhealthy for Sensitive Groups", color: "text-orange-500", description: "General public not likely affected, sensitive groups may experience irritation." };
  }
  if (aqi <= 200) {
    return { status: "Unhealthy", color: "text-red-500", description: "Everyone may begin to experience health effects." };
  }
  return { status: "Hazardous", color: "text-purple-600", description: "Health warning of emergency conditions." };
};

// Component level helper alias
export const getAQIStatus = (aqi) => getAqiStatus(aqi);