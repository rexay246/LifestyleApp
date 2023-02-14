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
class MainActivity : AppCompatActivity(), View.OnClickListener{
    private var mFullName: String? = null
    private var mFirstName: String? = null
    private var mLastName: String? = null

    private var mButtonSubmit: Button? = null
    private var mEtFullName: EditText? = null

    private var mSpinCity: Spinner? = null

    private var mSliderValue: TextView? = null
    private var mSlider: SeekBar? = null
    private var mAge: Double? = null

    private var mFeetSlider: SeekBar? = null
    private var mFeetValue: TextView? = null
    private var mFeet: Double? = null
    private var mInchSlider: SeekBar? = null
    private var mInchValue: TextView? = null
    private var mInch: Double? = null

    private var mWeightSlider: SeekBar? = null
    private var mWeightValue: TextView? = null
    private var mWeight: Double? = null

    private var mActivity: Spinner? = null
    private var mActivityChoice: Int? = null

    private var mRadioGroup: RadioGroup? = null

    private var mMbr: Double? = null
    private var mCalorieIntake: Double? = null

    private var mButtonCamera: Button? = null
    private var mThumbnail: Bitmap? = null
    private var mIvPic: ImageView? = null

    private var mDisplayIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mButtonSubmit = findViewById(R.id.button_submit)
        mButtonCamera = findViewById(R.id.btn_pp)

        mFeetSlider = findViewById(R.id.slider_feet)
        mInchSlider = findViewById(R.id.slider_inch)
        mFeetValue = findViewById(R.id.tv_feet_amount)
        mInchValue = findViewById(R.id.tv_inch_amount)
        mFeetSlider!!.max = 8
        mInchSlider!!.max = 11

        mSliderValue = findViewById(R.id.tv_age_slider)
        mSlider = findViewById(R.id.slider_age)
        mSlider!!.max = 126

        mWeightSlider = findViewById(R.id.slider_weight)
        mWeightValue = findViewById(R.id.tv_weight_slider)
        mWeightSlider!!.max = 500

        mRadioGroup = findViewById(R.id.radgroup)
        mRadioGroup!!.check(R.id.rad_male)

        mIvPic = findViewById(R.id.iv_pp)

        mActivity = findViewById(R.id.spinner_activity_level)

        sliderListener()

        addItemsOnSpinner2()

        mButtonSubmit!!.setOnClickListener(this)
        mButtonCamera!!.setOnClickListener(this)

        mDisplayIntent = Intent(this, DisplayActivity::class.java)
    }

    private fun sliderListener() {
        mSlider!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            // override the onProgressChanged method to perform operations
            // whenever the there a change in SeekBar
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mSliderValue!!.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        mFeetSlider!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            // override the onProgressChanged method to perform operations
            // whenever the there a change in SeekBar
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mFeetValue!!.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        mInchSlider!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            // override the onProgressChanged method to perform operations
            // whenever the there a change in SeekBar
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mInchValue!!.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        mWeightSlider!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            // override the onProgressChanged method to perform operations
            // whenever the there a change in SeekBar
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mWeightValue!!.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

    }

    private fun addItemsOnSpinner2() {
        mSpinCity = findViewById(R.id.spinner_city)
        val list: ArrayList<String> = ArrayList()
        list.add("list 1")
        list.add("list 2")
        list.add("list 3")
        val dataAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, list
        )
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mSpinCity?.adapter = dataAdapter
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_submit -> {

                mEtFullName = findViewById(R.id.et_name)
                mFullName = mEtFullName!!.text.toString()

                mWeight = mWeightValue!!.text.toString().toDouble()
                mAge = mSliderValue!!.text.toString().toDouble()
                mFeet = mFeetValue!!.text.toString().toDouble()
                mInch = mInchValue!!.text.toString().toDouble()

                mActivityChoice = mActivity?.selectedItemPosition.toString().toInt()

                //Check if the EditText string is empty
                if (mFullName.isNullOrBlank()) {
                    //Complain that there's no text
                    Toast.makeText(this, "Enter a name first!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    //Start an activity and pass the EditText string to it.
                    mFullName = mFullName!!.replace("^\\s+".toRegex(), "")
                    val splitstrings = mFullName!!.split("\\s+".toRegex()).toTypedArray()

                    when (splitstrings.size) {
                        1 -> {
                            Toast.makeText(
                                this,
                                "Enter both first and last name",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            mFirstName = splitstrings[0]
                            mLastName = splitstrings[splitstrings.size - 1]

                            mDisplayIntent!!.putExtra("FN_Data", mFirstName)
                            mDisplayIntent!!.putExtra("LN_Data", mLastName)
                        }
                    }
                }

                when (mRadioGroup!!.checkedRadioButtonId) {
                    R.id.rad_male ->
                        mMbr = 66 + (6.2 * mWeight!!) +
                                (12.7 * (12 * mFeet!! + mInch!!)) -
                                (6.76 * mAge!!)
                    R.id.rad_female ->
                        mMbr = 655.1 + (4.35 * mWeight!!) +
                                (4.7 * (12 * mFeet!! + mInch!!)) -
                                (4.7 * mAge!!)
                }
                mDisplayIntent!!.putExtra("BMR_Data", mMbr.toString())

                when (mActivityChoice) {
                    1 ->
                        mCalorieIntake = mMbr!! * 1.2
                    2 ->
                        mCalorieIntake = mMbr!! * 1.375
                    3 ->
                        mCalorieIntake = mMbr!! * 1.55
                    4 ->
                        mCalorieIntake = mMbr!! * 1.725
                    5 ->
                        mCalorieIntake = mMbr!! * 1.9
                }
                mDisplayIntent!!.putExtra("CALORIES_Data", mCalorieIntake.toString())
                startActivity(mDisplayIntent)


            }
            R.id.btn_pp -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    cameraActivity.launch(cameraIntent)
                } catch (ex:ActivityNotFoundException) {
                    /////
                }
            }
        }
    }

    private val cameraActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == RESULT_OK) {
            val extras = result.data!!.extras
            mThumbnail = extras!!["data"] as Bitmap?

            //Open a file and write to it
            if (isExternalStorageWritable) {
                val filePathString = saveImage(mThumbnail)
                mIvPic!!.setImageBitmap(mThumbnail)
                mDisplayIntent!!.putExtra("IMAGE_Data", filePathString)
            } else {
                Toast.makeText(this, "External storage not writable.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImage(finalBitmap: Bitmap?): String {
        val root = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val myDir = File("$root/saved_images")
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fname = "Thumbnail_$timeStamp.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            Toast.makeText(this, "file saved!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }
}