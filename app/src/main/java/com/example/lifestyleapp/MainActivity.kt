package com.example.lifestyleapp

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity(), InputFragment.SendDataInterface,
    DisplayFragment.SendDataInterface,  WeatherFragment.SendDataInterface,
    View.OnClickListener, UserRVAdapter.ListPasser {
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

    private var positionData: Int = -1

    private var newUser: Boolean = true

    private var saved: Boolean = false

    private val mWeatherViewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory((application as WeatherApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mWeatherViewModel.setLocation("tokyo")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnhome = findViewById(R.id.btn_home)
        btnedit = findViewById(R.id.btn_edit)
        btnhikes = findViewById(R.id.btn_hike)
        btnweather = findViewById(R.id.btn_weather)
        btnusers = findViewById(R.id.btn_users)


        if (savedInstanceState == null) {
            checkInputFragment = false
            checkDisplayFragment = false
            checkWeatherFragment = false
            checkListFragment = true
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
                if (!saved) {
                    Toast.makeText(this,"Please save data first", Toast.LENGTH_SHORT).show()
                }
                else if (!newUser) {
                    createDisplayFragment()
                }
                else {
                    Toast.makeText(this,"No User Found. Please make an account!", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btn_hike -> {
                if (!saved) {
                    Toast.makeText(this,"Please save data first", Toast.LENGTH_SHORT).show()
                }
                else if (!newUser) {
                    mWeatherViewModel.userData.observe(this, androidx.lifecycle.Observer { weatherData ->
                        if (weatherData != null) {
                            location = weatherData.location
                            latitude = weatherData.latitude
                            longitude = weatherData.longitude
                        }
                    })
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
                if (!saved) {
                    Toast.makeText(this,"Please save data first", Toast.LENGTH_SHORT).show()
                }
                else if (!newUser) {
                    sendDataWeather()
                }
                else {
                    Toast.makeText(this,"No User Found. Please make an account!", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btn_edit -> {
                if (!newUser) {
                    saved = false
                    sendBack()
                }
                else {
                    Toast.makeText(this,"No User Found. Please make an account!", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btn_users -> {
                if (!saved) {
                    Toast.makeText(this,"Please save data first", Toast.LENGTH_SHORT).show()
                }
                else {
                    // mCustomListData.updateItemChoice(activity_level.toString(), positionData)
                    userListFragment = UserListFragment()

                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_holder, userListFragment!!, "userlist_fragment")
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }
        }
    }

    override fun sendData() {
        saved = true
        createDisplayFragment()

        newUser = false

        checkDisplayFragment = true
        checkInputFragment = false
        checkWeatherFragment = false
        checkListFragment = false

    }

    override fun sendDataBack() {
    }

    fun sendBack() {
        inputFragment = InputFragment()
        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.fragment_holder, inputFragment!!, "input_fragment")
        transaction.addToBackStack(null)
        transaction.commit()

        checkListFragment = false
        checkWeatherFragment = false
        checkDisplayFragment = false
        checkInputFragment = true
    }

    override fun sendDataWeather() {
        mWeatherViewModel.userData.observe(this, androidx.lifecycle.Observer { weatherData ->
            if (weatherData != null) {
                location = weatherData.location
            }
        })
        val inputFromEt = location.toString().split(",")[0].replace(" ", "%20")
        mWeatherViewModel.setLocation(inputFromEt)

        weatherFragment = WeatherFragment()
        val transaction = supportFragmentManager.beginTransaction()

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
        outState.putInt("position_data", positionData)
        outState.putBoolean("saved_data", saved)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        checkInputFragment = savedInstanceState.getBoolean("input_data")
        checkDisplayFragment = savedInstanceState.getBoolean("display_data")
        checkWeatherFragment = savedInstanceState.getBoolean("weather_data")
        checkListFragment = savedInstanceState.getBoolean("list_data")
        newUser = savedInstanceState.getBoolean("new_data")
        positionData = savedInstanceState.getInt("position_data")
        saved = savedInstanceState.getBoolean("saved_data")

        super.onRestoreInstanceState(savedInstanceState)
    }

    companion object {
        private val mCustomListData = UsersData(10)
    }

    override fun passListData(position: Int) {
        if (position == 0) {
            mWeatherViewModel.setCurrentPosition(position)
            saved = false
            sendBack()
        }
        else {
            //Get the string data corresponding to the detail view
            mWeatherViewModel.setCurrentPosition(position)
            createDisplayFragment()
            newUser = false
            saved = true
        }
        positionData = position
    }
}