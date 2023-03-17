package com.example.lifestyleapp

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
    private var textBox : TextView? = null
    private var backButton : Button? = null
    private var location: String? = null

    private var str_location: String? = null

    val apiKey: String = "39866961fc4cbc2c09fc4bee1ceb454b"

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
        textBox = view.findViewById(R.id.tempText) as TextView
        val argumentBundle = arguments
        location = argumentBundle!!.getString("location_data")
        location = location!!.substring(0, location!!.indexOf(","))
        //backButton = view.findViewById(R.id.backButton) as Button
        WeatherTask().execute()

        //backButton!!.setOnClickListener(this)

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
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$location,us&units=imperial&appid=$apiKey")
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
                val main = jsonObj.getJSONObject("main")
                val temp = main.getString("temp")

                textBox!!.text = temp + "°F";

            }
            catch (e: Exception){
            }
        }
    }
}