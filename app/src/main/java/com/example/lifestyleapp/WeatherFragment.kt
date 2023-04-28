package com.example.lifestyleapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.loader.content.AsyncTaskLoader
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.net.URL
import kotlin.math.roundToInt

class WeatherFragment : Fragment(){

    private var data_sender : SendDataInterface? = null
    private var tempTextBox : TextView? = null
    private var cityTextBox : TextView? = null
    private var descTextBox: TextView? = null
    private var humTextBos: TextView? = null
    private var windTexBox: TextView? = null

    private var str_location: String? = null

    interface SendDataInterface {
        fun sendData()
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
        descTextBox = view.findViewById(R.id.descText) as TextView
        windTexBox = view.findViewById(R.id.windText) as TextView
        cityTextBox = view.findViewById(R.id.ctiyText) as TextView
        humTextBos = view.findViewById(R.id.humidityText) as TextView

        // WeatherTask().execute()
        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]
        model.data.observe(viewLifecycleOwner, Observer { weatherData -> // Update the UI if this data variable changes
            if (weatherData != null) {
                cityTextBox!!.text = "" + (weatherData.locationData!!.city)
                tempTextBox!!.text = "" + (weatherData.temperature.temp - 273.15).roundToInt() + " C"
                descTextBox!!.text = "" + (weatherData.currentCondition.condition)
                humTextBos!!.text = "Humidity: " + weatherData.currentCondition.humidity + "%"
                windTexBox!!.text = "Wind: " + weatherData.wind.speed + " mph"
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("location_data", str_location)
        super.onSaveInstanceState(outState)
    }

}