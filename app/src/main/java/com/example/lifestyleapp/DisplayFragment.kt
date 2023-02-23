package com.example.lifestyleapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class DisplayFragment : Fragment() {
    private var mTvFirstName: TextView? = null
    private var mTvLastName: TextView? = null
    private var str_first_name: String? = null
    private var str_last_name: String? = null

    private var mIvPic: ImageView? = null
    private var str_filepath: String? = null

    private var mTvBmrData: TextView? = null
    private var str_bmr: String? = null

    private var mTvActivityData: TextView? = null
    private var str_activity: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.display_fragment, container, false)

        mTvFirstName = view.findViewById(R.id.tv_fn_data) as TextView
        mTvLastName = view.findViewById(R.id.tv_ln_data) as TextView
        mTvBmrData = view.findViewById(R.id.tv_bmr_data) as TextView
        mTvActivityData = view.findViewById(R.id.tv_activity_data) as TextView
        mIvPic = view.findViewById(R.id.iv_pp) as ImageView

        if (savedInstanceState != null) {
            str_first_name = savedInstanceState.getString("first_data")
            str_last_name = savedInstanceState.getString("last_data")
            str_filepath = savedInstanceState.getString("filepath")
            str_bmr = savedInstanceState.getString("bmr_data")
            str_activity = savedInstanceState.getString("activity_data")
        } else {
            val argumentBundle = arguments
            str_first_name = argumentBundle!!.getString("first_name")
            str_last_name = argumentBundle.getString("last_name")
            str_filepath = argumentBundle.getString("filepath")
            str_bmr = argumentBundle.getString("bmr_data")
            str_activity = argumentBundle.getString("activity_data")
        }
        mTvFirstName!!.text = str_first_name
        mTvLastName!!.text = str_last_name
        mTvBmrData!!.text = str_bmr
        mTvActivityData!!.text = str_activity

        val thumbnailImage = BitmapFactory.decodeFile(str_filepath)
        if (thumbnailImage != null) {
            mIvPic!!.setImageBitmap(thumbnailImage)
        }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("first_data", str_first_name)
        outState.putString("last_data", str_last_name)
        outState.putString("filepath", str_filepath)
        outState.putString("bmr_data", str_bmr)
        outState.putString("activity_data", str_activity)
        super.onSaveInstanceState(outState)
    }
}