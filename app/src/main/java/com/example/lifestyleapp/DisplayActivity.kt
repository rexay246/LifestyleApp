package com.example.lifestyleapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class DisplayActivity : AppCompatActivity(){
    private var mTvFirstName: TextView? = null
    private var mTvLastName: TextView? = null
    private var mIvPic: ImageView? = null
    private var mTvBmrData: TextView? = null
    private var mTvActivityData: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        mTvFirstName = findViewById(R.id.tv_fn_data)
        mTvLastName = findViewById(R.id.tv_ln_data)
        mTvBmrData = findViewById(R.id.tv_bmr_data)
        mTvActivityData = findViewById(R.id.tv_activity_data)
        mIvPic = findViewById(R.id.iv_pp)

        val IntentReceived = intent

        mTvFirstName!!.text = IntentReceived.getStringExtra("FN_Data")
        mTvLastName!!.text = IntentReceived.getStringExtra("LN_Data")
        mTvBmrData!!.text = IntentReceived.getStringExtra("BMR_Data")
        mTvActivityData!!.text = IntentReceived.getStringExtra("CALORIES_Data")

        val imagePath = IntentReceived.getStringExtra("IMAGE_Data")
        val thumbnailImage = BitmapFactory.decodeFile(imagePath)
        if (thumbnailImage != null) {
            mIvPic!!.setImageBitmap(thumbnailImage)
        }
    }
}