package com.example.lifestyleapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.DecimalFormat
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.slider.Slider
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class InputFragment : Fragment(), View.OnClickListener,
    EasyPermissions.PermissionCallbacks, LocationListener {
    private var mFullName: String? = null
    private var mFirstName: String? = null
    private var mLastName: String? = null

    private var mButtonSubmit: Button? = null
    private var mEtFullName: EditText? = null

    private var mButtonLocation: Button? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var current_location: String? = null
    private var mTvLocation: TextView? = null

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
    private var mFilepath: String? = null
    private var mIvPic: ImageView? = null

    private var unchangeSaves: Boolean = false

    private var model: WeatherViewModel? = null

    private var data_sender : SendDataInterface? = null
    private lateinit var locationManager: LocationManager

    private var position: Int? = null
    private var weight: String? = null
    private var feet: String? = null
    private var inch: String? = null
    private var age: String? = null

    companion object {
        const val PERMISSION_LOCATION_REQUEST = 1
    }

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.input_fragment, container, false)

        mEtFullName = view.findViewById(R.id.et_name) as EditText

        mButtonSubmit = view.findViewById(R.id.button_submit) as Button

        mButtonCamera = view.findViewById(R.id.btn_pp) as Button

        mFeetSlider = view.findViewById(R.id.slider_feet) as SeekBar
        mInchSlider = view.findViewById(R.id.slider_inch) as SeekBar
        mFeetValue = view.findViewById(R.id.tv_feet_amount) as TextView
        mInchValue = view.findViewById(R.id.tv_inch_amount) as TextView
        mFeetSlider!!.max = 8
        mInchSlider!!.max = 11

        mSliderValue = view.findViewById(R.id.tv_age_slider) as TextView
        mSlider = view.findViewById(R.id.slider_age) as SeekBar
        mSlider!!.max = 126

        mWeightSlider = view.findViewById(R.id.slider_weight) as SeekBar
        mWeightValue = view.findViewById(R.id.tv_weight_slider) as TextView
        mWeightSlider!!.max = 500

        mRadioGroup = view.findViewById(R.id.radgroup) as RadioGroup
        mRadioGroup!!.check(R.id.rad_male)

        mIvPic = view.findViewById(R.id.iv_pp) as ImageView

        mActivity = view.findViewById(R.id.spinner_activity_level) as Spinner

        mButtonLocation = view.findViewById(R.id.button_map) as Button
        mTvLocation = view.findViewById(R.id.tv_current_location) as TextView

        locationManager = this.requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        sliderListener()


        mButtonSubmit!!.setOnClickListener(this)
        mButtonCamera!!.setOnClickListener(this)
        mButtonLocation!!.setOnClickListener(this)

        if (savedInstanceState != null) {
            mFilepath = savedInstanceState.getString("image_path")
            mThumbnail = BitmapFactory.decodeFile(mFilepath)
            mIvPic!!.setImageBitmap(mThumbnail)
            current_location = savedInstanceState.getString("location_data")
            mTvLocation!!.text = current_location
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]
        model!!.userData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { userData -> // Update the UI if this data variable changes
            if (userData != null) {

                if (userData.full_name == "New User")
                    mEtFullName!!.setText("")
                else
                    mEtFullName!!.setText(userData.full_name)

                if (userData.sex == "male") {
                    mRadioGroup!!.check(R.id.rad_male)
                }
                else {
                    mRadioGroup!!.check(R.id.rad_female)
                }

                weight = userData.weight!!
                mWeightSlider!!.progress = weight!!.toDouble().toInt()
                mWeightValue!!.text = weight

                feet = userData.feet!!
                mFeetSlider!!.progress = feet!!.toDouble().toInt()
                mFeetValue!!.text = feet

                inch = userData.inch!!
                mInchSlider!!.progress = inch!!.toDouble().toInt()
                mInchValue!!.text = inch

                age = userData.age!!
                mSlider!!.progress = age!!.toDouble().toInt()
                mSliderValue!!.text = age

                mActivity!!.setSelection(userData.activity_level!!.toDouble().toInt())

                mTvLocation!!.text = userData.location!!

                mFilepath = userData.filepath!!
                mThumbnail = BitmapFactory.decodeFile(mFilepath)
                mIvPic!!.setImageBitmap(mThumbnail)
            }
        })
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

    private fun hasLocationPermission() =
        EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    private fun requestLocationPermission() {
        EasyPermissions.requestPermissions(
            this,
            "Need to provide a location",
            PERMISSION_LOCATION_REQUEST,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {
            requestLocationPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(
            requireContext(),
            "Permission Granted",
            Toast.LENGTH_SHORT
        ).show()
    }

    @SuppressLint("MissingPermission")
    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_submit -> {
                mFullName = mEtFullName!!.text.toString()

                mWeight = mWeightValue!!.text.toString().toDouble()
                mAge = mSliderValue!!.text.toString().toDouble()
                mFeet = mFeetValue!!.text.toString().toDouble()
                mInch = mInchValue!!.text.toString().toDouble()

                mActivityChoice = mActivity?.selectedItemPosition.toString().toInt()
                if(mActivityChoice == null){
                    mActivityChoice = 0
                }

                val bundleList = mutableListOf<String?>()
                val df = DecimalFormat("#.###")

                //Check if the EditText string is empty
                if (mFullName.isNullOrBlank()) {
                    //Complain that there's no text
                    Toast.makeText(activity, "Enter a name first!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    //Start an activity and pass the EditText string to it.
                    mFullName = mFullName!!.replace("^\\s+".toRegex(), "")
                    val splitstrings = mFullName!!.split("\\s+".toRegex()).toTypedArray()

                    when (splitstrings.size) {
                        1 -> {
                            mFirstName = splitstrings[0]
                            mLastName = ""

                            bundleList.add(mFirstName)
                            bundleList.add(mLastName)
                            bundleList.add(mFullName)
                        }
                        else -> {
                            mFirstName = splitstrings[0]
                            mLastName = splitstrings[splitstrings.size - 1]

                            bundleList.add(mFirstName)
                            bundleList.add(mLastName)
                            bundleList.add(mFullName)
                        }
                    }
                }

                if (mWeight!! == 0.0) {
                    Toast.makeText(activity, "Enter a valid weight", Toast.LENGTH_SHORT)
                        .show()
                }

                if (mFeet!! == 0.0 && mInch!! == 0.0) {
                    Toast.makeText(activity, "Enter a valid height", Toast.LENGTH_SHORT)
                        .show()
                }

                if (mAge!! == 0.0) {
                    Toast.makeText(activity, "Enter a valid age", Toast.LENGTH_SHORT)
                        .show()
                }

                when (mRadioGroup!!.checkedRadioButtonId) {
                    R.id.rad_male -> {
                        mMbr = 66 + (6.2 * mWeight!!) +
                                (12.7 * (12 * mFeet!! + mInch!!)) -
                                (6.76 * mAge!!)
                        bundleList.add("male")
                    }
                    R.id.rad_female -> {
                        mMbr = 655.1 + (4.35 * mWeight!!) +
                                (4.7 * (12 * mFeet!! + mInch!!)) -
                                (4.7 * mAge!!)
                        bundleList.add("female")
                    }
                }
                bundleList.add(mWeight.toString())
                bundleList.add(mFeet.toString())
                bundleList.add(mInch.toString())
                bundleList.add(mAge.toString())
                bundleList.add(df.format(mMbr).toString())

                if (mActivityChoice == 0) {
                    Toast.makeText(activity, "Please select an activity choice", Toast.LENGTH_SHORT)
                        .show()
                }
                else {
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
                    bundleList.add(mActivityChoice.toString())
                    bundleList.add(df.format(mCalorieIntake).toString())
                }

                if (mTvLocation != null) {
                    if (mTvLocation!!.text == "Loading...") {
                        Toast.makeText(activity, "Please wait while we find your location", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else {
                        bundleList.add(mTvLocation!!.text.toString())
                        bundleList.add(longitude.toString())
                        bundleList.add(latitude.toString())
                    }
                }
                else {
                    Toast.makeText(activity, "Please enter a location", Toast.LENGTH_SHORT)
                        .show()
                }

                if (mFilepath != null && mFilepath != "0") {
                    bundleList.add(mFilepath)
                }
                else {
                    Toast.makeText(activity, "Please submit a photo", Toast.LENGTH_SHORT)
                        .show()
                }

                if (bundleList.size == 15) {
                    unchangeSaves = true
                    bundleList.add(unchangeSaves.toString())

                    if (model!!.currentPosition == 0) {
                        model!!.setUserData(bundleList.toTypedArray())
                    }
                    else {
                        model!!.updateUserData(bundleList.toTypedArray())
                    }
                    data_sender!!.sendData()
                }
            }
            R.id.btn_pp -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    cameraActivity.launch(cameraIntent)
                } catch (ex: ActivityNotFoundException) {
                    /////
                }
            }
            R.id.button_map -> {
                if (hasLocationPermission()) {
                    mTvLocation!!.text = "Loading..."
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        1L,1.0f, this)
                } else {
                    requestLocationPermission()
                }
            }
        }
    }

    private val cameraActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val extras = result.data!!.extras
            mThumbnail = extras!!["data"] as Bitmap?

            //Open a file and write to it
            if (isExternalStorageWritable) {
                mFilepath = saveImage(mThumbnail)
                mIvPic!!.setImageBitmap(mThumbnail)
            } else {
                Toast.makeText(activity, "External storage not writable.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImage(finalBitmap: Bitmap?): String {
        val root = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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
            Toast.makeText(activity, "file saved!", Toast.LENGTH_SHORT).show()
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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("image_path", mFilepath)
        outState.putString("location_data", current_location)
        super.onSaveInstanceState(outState)
    }

    override fun onLocationChanged(location: Location) {
        val geocoder = Geocoder(requireContext())
        latitude = location.latitude
        longitude = location.longitude
        val currentLocation = geocoder.getFromLocation(
            latitude!!,
            longitude!!,
            1)

        if (currentLocation!!.first().countryName == "United States") {
            mTvLocation!!.text = currentLocation!!.first().subAdminArea + ", " +
                    currentLocation!!.first().adminArea + ", " +
                    currentLocation!!.first().countryName
        }
        else {
            mTvLocation!!.text = currentLocation!!.first().adminArea + ", " +
                    currentLocation!!.first().countryName
        }

        current_location = mTvLocation!!.text.toString()

        locationManager.removeUpdates(this)
    }
}