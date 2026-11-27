🌦️ WeatherSense
Weather Intelligence. Smarter Decisions.
<p align="center">
<b>A Smart Weather Monitoring & Intelligence Platform</b>
<br/>
Real-time weather updates, air quality monitoring, agricultural insights and an intelligent weather assistant — all in one place.
</p>

<p align="center">
<img src="https://img.shields.io/badge/Frontend-React-61DAFB?style=for-the-badge&logo=react&logoColor=black" />
<img src="https://img.shields.io/badge/Backend-Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" />
<img src="https://img.shields.io/badge/Language-Java-orange?style=for-the-badge&logo=openjdk&logoColor=white" />
<img src="https://img.shields.io/badge/API-Open--Meteo-2563EB?style=for-the-badge" />
</p>

🌍 About The Project
WeatherSense is a full-stack weather intelligence application designed to provide users with useful weather information and environmental insights through a modern and interactive dashboard.

The application integrates a React frontend with a Spring Boot backend to retrieve weather information from external APIs and transform it into meaningful data.

Instead of simply displaying temperature, WeatherSense also provides air quality information, agricultural recommendations and an AI assistant experience.

✨ Key Features

Feature	Description
🌤️ Current Weather	Live temperature, feels-like temperature, humidity and wind speed
📅 5-Day Forecast	Daily maximum and minimum temperatures with weather conditions
🌙 Day & Night Visualization	Dynamic weather presentation based on daytime and nighttime conditions
🌫️ Air Quality	Air quality index and pollutant information
🌱 Agriculture Insights	Weather-based farming recommendations
🤖 AI Weather Assistant	Dedicated assistant interface
🔍 City Search	Search weather information by city
📊 Weather Statistics	Atmospheric pressure, UV index, visibility and humidity
🖥️ Application Preview
Add your actual application screenshots below.

Weather Dashboard	Weather Statistics
Add dashboard screenshot here	Add statistics screenshot here

Forecast & Air Quality	Agriculture Insights
Add forecast screenshot here	Add agriculture screenshot here
🛠️ Technology Stack
Frontend
React.js
JavaScript
CSS
HTML5
Fetch API
Backend
Java
Spring Boot
Spring Web
REST API
Jackson ObjectMapper
Spring RestClient
External APIs
Open-Meteo Weather API
Open-Meteo Geocoding API
🏗️ System Architecture
WeatherSense follows a layered architecture where the frontend communicates with the Spring Boot backend through REST endpoints.


mermaid
flowchart TD
    U["👤 User"] --> FE["🌐 React Frontend"]

    FE --> P["📄 Pages & Components"]
    P --> S["Weather API Service"]

    S -->|"HTTP Requests"| C["⚙️ Spring Boot REST API"]

    C --> CT["REST Controller"]
    CT --> SV["Weather Service"]

    SV --> CL["Weather API Client"]
    SV --> GL["Geocoding API Client"]

    CL --> WM["☀️ Open-Meteo Weather API"]
    GL --> GC["📍 Open-Meteo Geocoding API"]

    SV --> DTO["📦 DTO Responses"]
    DTO --> FE

    style U fill:#f59e0b,color:#111827
    style FE fill:#2563eb,color:#ffffff
    style C fill:#7c3aed,color:#ffffff
    style SV fill:#0d9488,color:#ffffff
    style WM fill:#059669,color:#ffffff
    style GC fill:#059669,color:#ffffff
    style DTO fill:#d97706,color:#ffffff
🔄 How It Works
The user searches for a city in the React application.
The frontend sends an HTTP request to the Spring Boot backend.
The backend uses the Geocoding API to find the city's coordinates.
The Weather API Client retrieves weather information using latitude and longitude.
The Weather Service processes the API response.
DTO objects structure the weather information.
The backend returns JSON responses to the frontend.
React components display the weather dashboard and insights.
📂 Project Structure
Frontend Architecture

text
frontend/
│
├── assets/
│
├── components/
│   ├── Navbar
│   ├── SearchBar
│   ├── CurrentWeather
│   ├── WeatherStats
│   ├── Forecast
│   ├── AirQuality
│   └── AgricultureCard
│
├── pages/
│   ├── Home
│   └── Assistant
│
├── services/
│   └── weatherApi
│
└── utils/
    └── weatherUtils
Folder responsibilities:

assets/ – Static assets and visual resources.
components/ – Reusable UI components.
pages/ – Main application pages.
services/ – API communication and data handling.
utils/ – Weather code mapping and AQI status utilities.
Backend Architecture

text
backend/
│
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/weathersense/
    │   │       │
    │   │       ├── client/
    │   │       │   ├── WeatherApiClient
    │   │       │   └── GeocodingApiClient
    │   │       │
    │   │       ├── config/
    │   │       │
    │   │       ├── controller/
    │   │       │
    │   │       ├── dto/
    │   │       │   ├── WeatherResponse
    │   │       │   ├── ForecastDay
    │   │       │   └── ForecastResponse
    │   │       │
    │   │       ├── exception/
    │   │       │
    │   │       ├── service/
    │   │       │   └── WeatherService
    │   │       │
    │   │       └── WeatherSenseApplication
    │   │
    │   └── resources/
    │       └── application.properties
    │
    └── test/
        └── java/
Backend responsibilities:

client/ – Communicates with external weather and geocoding APIs.
config/ – Contains application configuration.
controller/ – Exposes REST endpoints.
dto/ – Defines structured request and response objects.
exception/ – Handles application exceptions.
service/ – Contains weather processing and business logic.
resources/ – Stores application properties and resources.
test/ – Contains backend tests.
🌤️ Weather Data Integration
WeatherSense uses Open-Meteo to retrieve weather information.

Current Weather Data
The application retrieves:

Temperature
Apparent temperature
Relative humidity
Weather code
Wind speed
Visibility
Surface pressure
UV index
Day/night indicator
Forecast Data
The forecast integration retrieves:

Daily maximum temperature
Daily minimum temperature
Precipitation probability
Daily weather code
Geocoding
The Geocoding API converts a searched city name into geographical coordinates.

These coordinates are then used to retrieve weather information.

🌱 Agriculture Intelligence
WeatherSense includes a dynamic agriculture insight system.

The backend evaluates weather conditions such as:

Temperature
Humidity
Precipitation
Weather conditions
Based on these values, it generates farming recommendations.

Examples include:

🌧️ Rain detected: Delay fertilizer application and pesticide spraying.
🌡️ High temperature: Maintain soil moisture and schedule irrigation.
💧 High humidity: Monitor crops for possible fungal risks.
🌱 Favorable conditions: Suitable for standard agricultural activities.
Agricultural recommendations are general weather-based insights, not a substitute for professional agronomic advice.
