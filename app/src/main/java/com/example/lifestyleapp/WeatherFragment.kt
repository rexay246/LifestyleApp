package com.example.lifestyleapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.loader.content.AsyncTaskLoader
import org.json.JSONObject
import java.net.URL

class WeatherFragment : Fragment(), View.OnClickListener{

    private var data_sender : SendDataInterface? = null
    private var tempTextBox : TextView? = null
    private var cityTextBox : TextView? = null
    private var descTextBox: TextView? = null
    private var humTextBos: TextView? = null
    private var windTexBox: TextView? = null
    private var location: String? = null

    private var str_location: String? = null

    val apiKey: String = "f36b0d473419f1af3f06e013ee376e00"

    interface SendDataInterface {
        fun sendData(data: Array<String?>?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        data_sender = try {
            context as SendDataInterface
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement SubmitFragment.SendDataInterface")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.weather_fragment, container, false)
        tempTextBox = view.findViewById(R.id.tempText) as TextView
        cityTextBox = view.findViewById(R.id.ctiyText) as TextView
        descTextBox = view.findViewById(R.id.descriptionText) as TextView
        humTextBos = view.findViewById(R.id.humidityText) as TextView
        windTexBox = view.findViewById(R.id.windText) as TextView

        val argumentBundle = arguments
        location = argumentBundle!!.getString("location_data")
        location = location!!.substring(0, location!!.indexOf(","))

        cityTextBox!!.text = location

        tempTextBox!!.text = "Loading..."
        descTextBox!!.text = "Loading..."
        humTextBos!!.text = "Loading..."
        windTexBox!!.text = "Loading..."

        WeatherTask().execute()
        return view
    }

    override fun onClick(view: View) {
        when (view.id) {
//            R.id.backButton -> {
//                val bundleList = mutableListOf<String?>()
//                data_sender!!.sendData(bundleList.toTypedArray())
//            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("location_data", str_location)
        super.onSaveInstanceState(outState)
    }

    inner class WeatherTask() : AsyncTask<String, Void, String>()
    {
        override fun doInBackground(vararg p0: String?): String? {
            var response:String?

            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$location&units=imperial&appid=$apiKey")
                    .readText(Charsets.UTF_8)
            }
            catch(e: Exception){
                response = null;
            }

            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            try {
                val jsonObj = JSONObject(result)
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val main = jsonObj.getJSONObject("main")
                val temp = main.getString("temp")
                val description = weather.getString("description")
                val humidity = main.getString("humidity")
                val speed = wind.getString("speed")

                tempTextBox!!.text = temp + "°F"
                descTextBox!!.text = description
                humTextBos!!.text = "Humidity: $humidity%"
                windTexBox!!.text = "Wind: $speed mph"

            }
            catch (e: Exception){
            }
        }
    }
}