package com.example.lifestyleapp


import kotlin.Throws
import org.json.JSONException
import org.json.JSONObject


//Declare methods as static. We don't want to create objects of this class.
object JSONWeatherUtils {
    @Throws(JSONException::class)
    fun getWeatherData(data: String?): WeatherData {
        val weatherData = WeatherData()

        //Start parsing JSON data
        val jsonObject = JSONObject(data!!) //Must throw JSONException
        val currentCondition = weatherData.currentCondition
        val jsonMain = jsonObject.getJSONObject("main")
        val jsonWeather = jsonObject.getJSONArray("weather")
        currentCondition.humidity = jsonMain.getInt("humidity").toDouble()
        currentCondition.pressure = jsonMain.getInt("pressure").toDouble()
        currentCondition.condition = jsonWeather.getJSONObject(0).getString("description")
        weatherData.currentCondition = currentCondition

        val location_data = LocationData()
        location_data.city = jsonObject.getString("name")
        weatherData.locationData = location_data

        //Get the temperature, wind and cloud data.
        val temperature = weatherData.temperature
        val wind = weatherData.wind
        val clouds = weatherData.clouds
        temperature.maxTemp = jsonMain.getDouble("temp_max")
        temperature.minTemp = jsonMain.getDouble("temp_min")
        temperature.temp = jsonMain.getDouble("temp")
        weatherData.temperature = temperature

        val jsonWind = jsonObject.getJSONObject("wind")
        wind.speed = jsonWind.getDouble("speed")
        weatherData.wind = wind
        return weatherData
    }
}