package com.example.lifestyleapp

import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import androidx.annotation.WorkerThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlin.jvm.Synchronized

class WeatherRepository private constructor(weatherDao: WeatherDao, userDao: UserDao) {
    // This LiveData object that is notified when we've fetched the weather
    val data = MutableLiveData<WeatherData>()
    val userData = MutableLiveData<UserData>()

    // This flow is triggered when any change happens to the database
    val allCityWeather: Flow<List<WeatherTable>> = weatherDao.getAllWeather()
    val allUser: Flow<List<UserTable>> = userDao.getAllUser()

    private var mLocation: String? = null
    private var mJsonString: String? = null
    private var mWeatherDao: WeatherDao = weatherDao
    private var mUserDao: UserDao = userDao
    private var userDataArray: Array<String?>? = null
    var size: Int = 0
    var current_position: Int = 0

    fun setCurrentPosition(position: Int) {
        current_position = position
        mScope.launch(Dispatchers.IO) {
            val newUser = allUser.first()[position]
            val bundleList = mutableListOf<String?>()
            bundleList.add(newUser.user_name)
            bundleList.add(newUser.last_name)
            bundleList.add(newUser.full_name)
            bundleList.add(newUser.sex)
            bundleList.add(newUser.weight)
            bundleList.add(newUser.feet)
            bundleList.add(newUser.inch)
            bundleList.add(newUser.age)
            bundleList.add(newUser.mbr)
            bundleList.add(newUser.activity_level)
            bundleList.add(newUser.calorie_intake)
            bundleList.add(newUser.location)
            bundleList.add(newUser.longitude)
            bundleList.add(newUser.latitude)
            bundleList.add(newUser.filename)
            updateUserData(bundleList.toTypedArray())
        }
    }

    fun setLocation(location: String) {
        // First cache the location
        mLocation = location

        // Everything within the scope happens logically sequentially
        mScope.launch(Dispatchers.IO){
            //fetch data on a worker thread
            fetchAndParseWeatherData(location)

            // After the suspend function returns, Update the View THEN insert into db
            if(mJsonString!=null) {
                // Populate live data object. But since this is happening in a background thread (the coroutine),
                // we have to use postValue rather than setValue. Use setValue if update is on main thread
                data.postValue( JSONWeatherUtils.getWeatherData(mJsonString))

                // insert into db. This will trigger a flow
                // that updates a recyclerview. All db ops should happen
                // on a background thread
                insert()
            }
        }
    }

    fun setUserData(array_data: Array<String?>?) {
        userDataArray = array_data
        userData.postValue(getUserData(userDataArray))
        mScope.launch(Dispatchers.IO) {
            insertData()
            size += 1
        }
    }

    fun updateUserData(array_data: Array<String?>?) {
        userDataArray = array_data
        userData.postValue(getUserData(userDataArray))
        mScope.launch(Dispatchers.IO) {
            updateData()
        }
    }

    fun updateUserData() {
        userData.postValue(getUserData(userDataArray))
        mScope.launch(Dispatchers.IO) {
            updateData()
        }
    }

    @WorkerThread
    suspend fun insert() {
        if (mLocation != null && mJsonString!=null) {
            mWeatherDao.insert(WeatherTable(mLocation!!,mJsonString!!))
        }
    }

    @WorkerThread
    suspend fun insertData() {
        size = allUser.first().size + 1
        if (size == 1) {
            size = 2
        }
        current_position = size
        mUserDao.insert(UserTable(
            size,
            userDataArray!![0]!!,
            userDataArray!![1]!!,
            userDataArray!![2]!!,
            userDataArray!![3]!!,
            userDataArray!![4]!!,
            userDataArray!![5]!!,
            userDataArray!![6]!!,
            userDataArray!![7]!!,
            userDataArray!![8]!!,
            userDataArray!![9]!!,
            userDataArray!![10]!!,
            userDataArray!![11]!!,
            userDataArray!![12]!!,
            userDataArray!![13]!!,
            userDataArray!![14]!!))
    }

    @WorkerThread
    suspend fun updateData() {
        mUserDao.updateUser(UserTable(
            current_position + 1,
            userDataArray!![0]!!,
            userDataArray!![1]!!,
            userDataArray!![2]!!,
            userDataArray!![3]!!,
            userDataArray!![4]!!,
            userDataArray!![5]!!,
            userDataArray!![6]!!,
            userDataArray!![7]!!,
            userDataArray!![8]!!,
            userDataArray!![9]!!,
            userDataArray!![10]!!,
            userDataArray!![11]!!,
            userDataArray!![12]!!,
            userDataArray!![13]!!,
            userDataArray!![14]!!))
    }

    fun getUserData(array_data: Array<String?>?): UserData {
        val userInfo = UserData()

        userInfo.first_name = array_data!![0]
        userInfo.last_name = array_data[1]
        userInfo.full_name = array_data[2]
        userInfo.sex = array_data[3]
        userInfo.weight = array_data[4]
        userInfo.feet = array_data[5]
        userInfo.inch = array_data[6]
        userInfo.age = array_data[7]
        userInfo.mbr = array_data[8]
        userInfo.activity_level = array_data[9]
        userInfo.calorie_intake = array_data[10]
        userInfo.location = array_data[11]
        userInfo.longitude = array_data[12]
        userInfo.latitude = array_data[13]
        userInfo.filepath = array_data[14]

        return userInfo
    }

    @WorkerThread
    suspend fun fetchAndParseWeatherData(location: String) {
        val weatherDataURL = NetworkUtils.buildURLFromString(location)
        if(weatherDataURL!=null) {
            // This is actually a blocking call unless you're using an
            // asynchronous IO library (which we're not). However, it is a blocking
            // call on a background thread, not on the UI thread
            val jsonWeatherData = NetworkUtils.getDataFromURL(weatherDataURL)
            if (jsonWeatherData != null) {
                mJsonString = jsonWeatherData
            }
        }
    }

    // Make the repository singleton. Could in theory
    // make this an object class, but the companion object approach
    // is nicer (imo)
    companion object {
        private var mInstance: WeatherRepository? = null
        private lateinit var mScope: CoroutineScope
        @Synchronized
        fun getInstance(weatherDao: WeatherDao, userDao: UserDao,
        scope: CoroutineScope
        ): WeatherRepository {
            mScope = scope
            return mInstance?: synchronized(this){
                val instance = WeatherRepository(weatherDao, userDao)
                mInstance = instance
                instance
            }
        }
    }
}