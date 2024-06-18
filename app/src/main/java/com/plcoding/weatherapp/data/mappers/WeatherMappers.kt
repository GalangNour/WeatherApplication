package com.plcoding.weatherapp.data.mappers

import com.plcoding.weatherapp.data.remote.WeatherDataDto
import com.plcoding.weatherapp.data.remote.WeatherDto
import com.plcoding.weatherapp.domain.weather.WeatherData
import com.plcoding.weatherapp.domain.weather.WeatherInfo
import com.plcoding.weatherapp.domain.weather.WeatherType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


private class IndexedDataWeather(
    val index: Int,
    val data: WeatherData
)

fun WeatherDataDto.toWeatherDataMap() : Map<Int, List<WeatherData>>{
    return time.mapIndexed{ index, time ->
        val temperatures = temperatures[index]
        val weatherCodes = weatherCodes[index]
        val windSpeeds = windSpeeds[index]
        val pressures = pressures[index]
        val humidities = humidities[index]
        IndexedDataWeather(
            index = index,
            data = WeatherData(
                    time = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME),
                    temperatureCelcius = temperatures,
                    pressure = pressures,
                    windSpeed = windSpeeds,
                    humidity = humidities,
                    weatherType = WeatherType.fromWMO(weatherCodes)
                )
        )
    }.groupBy {
        it.index / 24
    }.mapValues {
        it.value.map { it.data }
    }
}

fun WeatherDto.toWeatherInfo() : WeatherInfo {
    val weatherDataMap = weatherData.toWeatherDataMap()
    val now = LocalDateTime.now()
    val currentWeatherData = weatherDataMap[0]?.find {
        val hour = if (now.minute < 30) now.hour else now.hour + 1
        it.time.hour == hour
    }
    return WeatherInfo(
        weatherDataPerDay = weatherDataMap,
        currentWeatherData = currentWeatherData
    )

}