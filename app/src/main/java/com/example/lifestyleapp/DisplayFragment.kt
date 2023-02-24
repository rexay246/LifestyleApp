package com.example.lifestyleapp

import android.content.Context
import android.graphics.BitmapFactory
import android.icu.text.DecimalFormat
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.loader.content.AsyncTaskLoader

class DisplayFragment : Fragment(), View.OnClickListener{
    private var mTvFirstName: TextView? = null
    private var str_first_name: String? = null
    private var str_last_name: String? = null

    private var mIvPic: ImageView? = null
    private var str_filepath: String? = null

    private var mTvBmrData: TextView? = null
    private var str_bmr: String? = null

    private var mTvActivityData: TextView? = null
    private var str_activity: String? = null

    private var mEditButton: Button? = null

    private var mSpinActivityChoice: Spinner? = null

    private var data_sender : SendDataInterface? = null

    private var choice: Double? = null

    private var str_location: String? = null

    val API: String = "9faee2c5de2d26f2d04a0a1be8d67d9c"

    interface SendDataInterface {
        fun sendDataBack(data: Array<String?>?)
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
        mEditButton = view.findViewById(R.id.btn_edit_user) as Button
        mSpinActivityChoice = view.findViewById(R.id.spinner_activity_level) as Spinner

        if (savedInstanceState != null) {
            str_first_name = savedInstanceState.getString("first_data")
            str_last_name = savedInstanceState.getString("last_data")
            str_filepath = savedInstanceState.getString("filepath")
            str_bmr = savedInstanceState.getString("bmr_data")
            str_activity = savedInstanceState.getString("calorie_data")
            str_location = savedInstanceState.getString("location_data")
        } else {
            val argumentBundle = arguments
            str_first_name = argumentBundle!!.getString("first_name")
            str_last_name = argumentBundle.getString("last_name")
            str_filepath = argumentBundle.getString("filepath")
            str_bmr = argumentBundle.getString("bmr_data")
            str_activity = argumentBundle.getString("calorie_data")
            mSpinActivityChoice!!.setSelection(argumentBundle.getString("activity_data")!!.toDouble().toInt())
            str_location = argumentBundle.getString("location_data")
        }
        mTvFirstName!!.text = str_first_name
        mTvBmrData!!.text = str_bmr
        mTvActivityData!!.text = str_activity

        val thumbnailImage = BitmapFactory.decodeFile(str_filepath)
        if (thumbnailImage != null) {
            mIvPic!!.setImageBitmap(thumbnailImage)
        }

        spinnerLister()

        mEditButton!!.setOnClickListener(this)

        return view
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_edit_user -> {
                val bundleList = mutableListOf<String?>()
                bundleList.add(choice.toString())
                data_sender!!.sendDataBack(bundleList.toTypedArray())
            }
        }
    }

    private fun spinnerLister() {
        mSpinActivityChoice!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
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
                choice = position.toDouble()

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
        super.onSaveInstanceState(outState)
    }
}