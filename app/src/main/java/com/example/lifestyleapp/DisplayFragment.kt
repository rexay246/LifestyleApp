package com.example.lifestyleapp

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.icu.text.DecimalFormat
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.loader.content.AsyncTaskLoader
import kotlin.math.roundToInt

class DisplayFragment : Fragment(){
    private var mTvFirstName: TextView? = null
    private var str_first_name: String? = null
    private var str_last_name: String? = null
    private var str_longitude: String? = null
    private var str_latitude: String? = null

    private var mIvPic: ImageView? = null
    private var str_filepath: String? = null

    private var mTvBmrData: TextView? = null
    private var str_bmr: String? = null

    private var mTvActivityData: TextView? = null
    private var str_activity: String? = null

    private var mSpinActivityChoice: Spinner? = null

    private var data_sender : SendDataInterface? = null

    private var choice: Double? = null

    private var str_location: String? = null

    private var model: WeatherViewModel? = null

    interface SendDataInterface {
        fun sendDataBack()
        fun sendDataWeather()
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
        val view = inflater.inflate(R.layout.display_fragment, container, false)

        mTvFirstName = view.findViewById(R.id.tv_fn_data) as TextView
        mTvBmrData = view.findViewById(R.id.tv_bmr_data) as TextView
        mTvActivityData = view.findViewById(R.id.tv_activity_data) as TextView
        mIvPic = view.findViewById(R.id.iv_pp) as ImageView
        mSpinActivityChoice = view.findViewById(R.id.spinner_activity_level) as Spinner

        spinnerLister()

        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]
        model!!.userData.observe(viewLifecycleOwner, Observer { userData -> // Update the UI if this data variable changes
            if (userData != null) {

                str_first_name = userData.first_name
                str_last_name = userData.last_name
                str_filepath = userData.filepath
                str_bmr = userData.mbr
                str_activity = userData.calorie_intake
                str_location = userData.location
                str_longitude = userData.longitude
                str_latitude = userData.latitude
                mSpinActivityChoice!!.setSelection(userData.activity_level!!.toDouble().toInt() - 1)

                mTvFirstName!!.text = str_first_name
                mTvBmrData!!.text = str_bmr
                mTvActivityData!!.text = str_activity

                val thumbnailImage = BitmapFactory.decodeFile(str_filepath)
                if (thumbnailImage != null) {
                    mIvPic!!.setImageBitmap(thumbnailImage)
                }
            }
        })
    }

    private fun spinnerLister() {
        mSpinActivityChoice!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position + 1) {
                    1 ->
                        str_activity = (str_bmr!!.toDouble() * 1.2).toString()
                    2 ->
                        str_activity = (str_bmr!!.toDouble() * 1.375).toString()
                    3 ->
                        str_activity = (str_bmr!!.toDouble() * 1.55).toString()
                    4 ->
                        str_activity = (str_bmr!!.toDouble() * 1.725).toString()
                    5 ->
                        str_activity = (str_bmr!!.toDouble() * 1.9).toString()
                }

                choice = position.toDouble() + 1
                val bundleList = mutableListOf<String?>()
                bundleList.add(choice.toString())
                model!!.userData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { userData -> // Update the UI if this data variable changes
                    if (userData != null) {
                        userData.activity_level = choice.toString()
                    }
                })
//                data_sender!!.sendDataBack()
                val df = DecimalFormat("#.###")
                mTvActivityData!!.text = df.format(str_activity!!.toDouble())
            }

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("first_data", str_first_name)
        outState.putString("last_data", str_last_name)
        outState.putString("filepath", str_filepath)
        outState.putString("bmr_data", str_bmr)
        outState.putString("activity_data", str_activity)
        outState.putString("location_data", str_location)
        outState.putString("longitude_data", str_longitude)
        outState.putString("latitude_data", str_latitude)
        super.onSaveInstanceState(outState)
    }
}