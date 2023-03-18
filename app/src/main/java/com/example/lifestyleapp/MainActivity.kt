package com.example.lifestyleapp

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
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
    DisplayFragment.SendDataInterface,  WeatherFragment.SendDataInterface,
    MyRVAdapter.ListPasser, View.OnClickListener {
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
    private var longitude: String? = null
    private var latitude: String? = null

    private var inputFragment: InputFragment? = null
    private var displayFragment: DisplayFragment? = null
    private var weatherFragment: WeatherFragment? = null
    private var userListFragment: UserListFragment? = null

    private var checkInputFragment: Boolean? = null
    private var checkDisplayFragment: Boolean? = null
    private var checkWeatherFragment: Boolean = false
    private var checkListFragment: Boolean = false

    private var btnhome: Button? = null
    private var btnedit: Button? = null
    private var btnhikes: Button? = null
    private var btnweather: Button? = null
    private var btnusers: Button? = null

    private var newUser: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnhome = findViewById(R.id.btn_home)
        btnedit = findViewById(R.id.btn_edit)
        btnhikes = findViewById(R.id.btn_hike)
        btnweather = findViewById(R.id.btn_weather)
        btnusers = findViewById(R.id.btn_users)

        if (savedInstanceState == null) {
            checkInputFragment = true
            checkDisplayFragment = false
            checkWeatherFragment = false
            checkListFragment = false
            first_name = ""
            last_name = ""
            full_name = ""
            sex = "1"
            weight = "0"
            feet = "0"
            inch = "0"
            age = "0"
            mbr = "0"
            activity_level = "0"
            calorie_intake = "0"
            location= ""
            filepath = ""
            longitude = "0"
            latitude = "0"
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

        userListFragment = if (savedInstanceState != null) {
            supportFragmentManager.findFragmentByTag("userlist_fragment") as UserListFragment?
        } else {
            UserListFragment()
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
        else if (checkWeatherFragment){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_holder, weatherFragment!!, "weather_fragment")
            transaction.addToBackStack(null)
            transaction.commit()
        }
        else if (checkListFragment) {
            val fragmentBundle = Bundle()
            fragmentBundle.putParcelable("user_list", mCustomListData)
            userListFragment!!.arguments = fragmentBundle
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_holder, userListFragment!!, "userlist_fragment")
            transaction.addToBackStack(null)
            transaction.commit()
        }

        btnweather!!.setOnClickListener(this)
        btnhikes!!.setOnClickListener(this)
        btnhome!!.setOnClickListener(this)
        btnedit!!.setOnClickListener(this)
        btnusers!!.setOnClickListener(this)
    }

    @SuppressLint("MissingPermission")
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_home -> {
                if (!newUser)
                    createDisplayFragment()
                else {
                    Toast.makeText(this,"No User Found. Please make an account!", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btn_hike -> {
                if (!newUser) {
                    val mSearchString = "Find Hikes Near $location"
                    val searchUri = Uri.parse("geo:$longitude,$latitude?q=$mSearchString")
                    //Create the implicit intent
                    val mapIntent = Intent(Intent.ACTION_VIEW, searchUri)
                    //If there's an activity associated with this intent, launch it
                    try{
                        startActivity(mapIntent)
                    }catch(ex: ActivityNotFoundException){
                        //handle errors here
                    }
                }
                else {
                    Toast.makeText(this,"No User Found. Please make an account!", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btn_weather -> {
                if (!newUser) {
                    val bundleList = mutableListOf<String?>()
                    sendDataWeather(bundleList.toTypedArray())
                }
                else {
                    Toast.makeText(this,"No User Found. Please make an account!", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btn_edit -> {
                if (!newUser) {
                    val bundleList = mutableListOf<String?>()
                    sendDataBack(bundleList.toTypedArray())
                }
                else {
                    Toast.makeText(this,"No User Found. Please make an account!", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btn_users -> {
                userListFragment = UserListFragment()
                val fragmentBundle = Bundle()
                fragmentBundle.putParcelable("user_list", mCustomListData)
                userListFragment!!.arguments = fragmentBundle
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_holder, userListFragment!!, "userlist_fragment")
                transaction.addToBackStack(null)
                transaction.commit()
            }
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
        longitude = data[12]
        latitude = data[13]
        filepath = data[14]

        if (newUser)
            mCustomListData.setItem(data)

        createDisplayFragment()

        newUser = false

        checkDisplayFragment = true
        checkInputFragment = false
        checkWeatherFragment = false
        checkListFragment = false
    }

    override fun sendDataBack(data: Array<String?>?) {
        inputFragment = InputFragment()
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

        checkListFragment = false
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

        checkListFragment = false
        checkWeatherFragment = true
        checkDisplayFragment = false
        checkInputFragment = false
    }

    fun createDisplayFragment() {
        displayFragment = DisplayFragment()

        val transaction = supportFragmentManager.beginTransaction()
        val sendData = Bundle()
        sendData.putString("first_name", first_name)
        sendData.putString("last_name", last_name)
        sendData.putString("filepath", filepath)
        sendData.putString("bmr_data", mbr)
        sendData.putString("activity_data", activity_level)
        sendData.putString("calorie_data", calorie_intake)
        sendData.putString("location_data", location)
        sendData.putString("longitude_data", longitude)
        sendData.putString("latitude_data", latitude)
        displayFragment!!.arguments = sendData

        transaction.replace(R.id.fragment_holder, displayFragment!!, "display_fragment")
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("display_data", checkDisplayFragment!!)
        outState.putBoolean("input_data", checkInputFragment!!)
        outState.putBoolean("weather_data", checkWeatherFragment)
        outState.putBoolean("list_data", checkListFragment)
        outState.putBoolean("new_data", newUser)

        outState.putString("first_data", first_name!!)
        outState.putString("last_data", last_name!!)
        outState.putString("full_data", full_name!!)
        outState.putString("sex_data", sex!!)
        outState.putString("weight_data", weight!!)
        outState.putString("feet_data", feet!!)
        outState.putString("inch_data", inch!!)
        outState.putString("age_data", age!!)
        outState.putString("mbr_data", mbr!!)
        outState.putString("activity_data", activity_level!!)
        outState.putString("calorie_data", calorie_intake!!)
        outState.putString("location_data", location!!)
        outState.putString("filepath_data", filepath!!)
        outState.putString("longitude_data", longitude!!)
        outState.putString("latitude_data", latitude!!)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        checkInputFragment = savedInstanceState.getBoolean("input_data")
        checkDisplayFragment = savedInstanceState.getBoolean("display_data")
        checkWeatherFragment = savedInstanceState.getBoolean("weather_data")
        checkListFragment = savedInstanceState.getBoolean("list_data")
        newUser = savedInstanceState.getBoolean("new_data")

        first_name = savedInstanceState.getString("first_data")
        last_name = savedInstanceState.getString("last_data")
        full_name = savedInstanceState.getString("full_data")
        sex = savedInstanceState.getString("sex_data")
        weight = savedInstanceState.getString("weight_data")
        feet = savedInstanceState.getString("feet_data")
        inch = savedInstanceState.getString("inch_data")
        age = savedInstanceState.getString("age_data")
        mbr = savedInstanceState.getString("mbr_data")
        activity_level = savedInstanceState.getString("activity_data")
        calorie_intake = savedInstanceState.getString("calorie_data")
        location = savedInstanceState.getString("location_data")
        longitude = savedInstanceState.getString("longitude_data")
        latitude = savedInstanceState.getString("latitude_data")
        filepath = savedInstanceState.getString("filepath_data")
        super.onRestoreInstanceState(savedInstanceState)
    }

    companion object {
        private val mCustomListData = UsersData(10)
    }

    override fun passListData(position: Int) {
        //Get the string data corresponding to the detail view
        if (position == 0) {
            first_name = ""
            last_name = ""
            full_name = ""
            sex = "1"
            weight = "0"
            feet = "0"
            inch = "0"
            age = "0"
            mbr = "0"
            activity_level = "0"
            calorie_intake = "0"
            location= ""
            filepath = ""
            longitude = "0"
            latitude = "0"

            val bundleList = mutableListOf<String?>()
            sendDataBack(bundleList.toTypedArray())
            newUser = true
        }
        else {
            val data = mCustomListData.getItemDetail(position - 1)
            first_name = data[0]
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
            longitude = data[12]
            latitude = data[13]
            filepath = data[14]
            createDisplayFragment()
        }
    }

}