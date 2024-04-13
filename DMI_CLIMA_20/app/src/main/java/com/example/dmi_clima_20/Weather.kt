package com.example.dmi_clima_20

data class Weather(
    val main: Main,
    val weather: List<WeatherDetail>
)

data class Main(
    val temp: Double,
    val humidity: Int
)

data class WeatherDetail(
    val main: String,
    val description: String
)
