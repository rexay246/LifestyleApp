package com.example.lifestyleapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
class MainActivity : AppCompatActivity(), InputFragment.SendDataInterface,
    DisplayFragment.SendDataInterface,  WeatherFragment.SendDataInterface{
    private var first_name: String? = null
    private var last_name: String? = null
    private var full_name: String? = null
    private var sex: String? = null
    private var weight: String? = null
    private var feet: String? = null
    private var inch: String? = null
    private var age: String? = null
    private var mbr: String? = null
    private var activity_level: String? = null
    private var calorie_intake: String? = null
    private var location: String? = null
    private var filepath: String? = null

    private var inputFragment: InputFragment? = null
    private var displayFragment: DisplayFragment? = null
    private var weatherFragment: WeatherFragment? = null
    private var checkInputFragment: Boolean? = null
    private var checkDisplayFragment: Boolean? = null
    private var checkWeatherFragment: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            checkInputFragment = true
            checkDisplayFragment = false
            checkWeatherFragment = false
        }

        inputFragment = if (savedInstanceState != null) {
            supportFragmentManager.findFragmentByTag("input_fragment") as InputFragment?
        } else {
            InputFragment()
        }
        displayFragment = if (savedInstanceState != null) {
            supportFragmentManager.findFragmentByTag("display_fragment") as DisplayFragment?
        } else {
            DisplayFragment()
        }

        weatherFragment = if (savedInstanceState != null) {
            supportFragmentManager.findFragmentByTag("weather_fragment") as WeatherFragment?
        } else {
            WeatherFragment()
        }


        if (checkInputFragment == true) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_holder, inputFragment!!, "input_fragment")
            transaction.addToBackStack(null)
            transaction.commit()
        }
        else if (checkDisplayFragment == true) {
            createDisplayFragment()
        }
        else if (checkWeatherFragment == true){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_holder, weatherFragment!!, "weather_fragment")
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun sendData(data: Array<String?>?) {
            first_name = data!![0]
            last_name = data[1]
            full_name = data[2]
            sex = data[3]
            weight = data[4]
            feet = data[5]
            inch = data[6]
            age = data[7]
            mbr = data[8]
            activity_level = data[9]
            calorie_intake = data[10]
            location = data[11]
            filepath = data[12]

            displayFragment = DisplayFragment()
            createDisplayFragment()

            checkDisplayFragment = true
            checkInputFragment = false
            checkWeatherFragment = false
    }

    override fun sendDataBack(data: Array<String?>?) {
        inputFragment = InputFragment()
        activity_level = data!![0]
        val transaction = supportFragmentManager.beginTransaction()
        val sendDataBack = Bundle()

        sendDataBack.putString("full_name", full_name)
        sendDataBack.putString("sex_data", sex)
        sendDataBack.putString("weight_data", weight)
        sendDataBack.putString("feet_data", feet)
        sendDataBack.putString("inch_data", inch)
        sendDataBack.putString("age_data", age)
        sendDataBack.putString("activity_data", activity_level)
        sendDataBack.putString("location_data", location)
        sendDataBack.putString("image_data", filepath)
        inputFragment!!.arguments = sendDataBack

        transaction.replace(R.id.fragment_holder, inputFragment!!, "input_fragment")
        transaction.addToBackStack(null)
        transaction.commit()

        checkWeatherFragment = false
        checkDisplayFragment = false
        checkInputFragment = true
    }

    override fun sendDataWeather(data: Array<String?>?) {
        weatherFragment = WeatherFragment()
        val transaction = supportFragmentManager.beginTransaction()
        val sendData = Bundle()
        sendData.putString("location_data", location)
        weatherFragment!!.arguments = sendData

        transaction.replace(R.id.fragment_holder, weatherFragment!!, "weather_fragment")
        transaction.addToBackStack(null)
        transaction.commit()

        checkWeatherFragment = true
        checkDisplayFragment = false
        checkInputFragment = false
    }

//    override fun sendDataBackWeather(data: Array<String?>?) {
//        displayFragment = DisplayFragment()
//        val transaction = supportFragmentManager.beginTransaction()
//
//        transaction.replace(R.id.fragment_holder, displayFragment!!, "display_fragment")
//        transaction.popBackStack();
//        transaction.addToBackStack(null)
//        transaction.commit()
//
//        checkDisplayFragment = true
//        checkInputFragment = false
//        checkWeatherFragment = false
//    }


    fun createDisplayFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        val sendData = Bundle()
        sendData.putString("first_name", first_name)
        sendData.putString("last_name", last_name)
        sendData.putString("filepath", filepath)
        sendData.putString("bmr_data", mbr)
        sendData.putString("activity_data", activity_level)
        sendData.putString("calorie_data", calorie_intake)
        sendData.putString("location_data", location)
        displayFragment!!.arguments = sendData

        transaction.replace(R.id.fragment_holder, displayFragment!!, "display_fragment")
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("display_data", checkDisplayFragment!!)
        outState.putBoolean("input_data", checkInputFragment!!)
        outState.putBoolean("weather_data", checkInputFragment!!)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        checkInputFragment = savedInstanceState.getBoolean("input_data")
        checkDisplayFragment = savedInstanceState.getBoolean("display_data")
        checkWeatherFragment = savedInstanceState.getBoolean("weather_data")

        super.onRestoreInstanceState(savedInstanceState)
    }
}