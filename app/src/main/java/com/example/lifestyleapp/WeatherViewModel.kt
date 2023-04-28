package com.example.lifestyleapp

import android.widget.ArrayAdapter
import androidx.lifecycle.*
import kotlinx.coroutines.flow.count

class WeatherViewModel(repository: WeatherRepository) : ViewModel() {
    // Connect a live data object to the current bit of weather info
    private val jsonData: LiveData<WeatherData> = repository.data
    private val user_data: LiveData<UserData> = repository.userData

    //Use a second live data here to show entire contents of db
    // This casts a flow in the repo as a live data so an observer in the view
    // can watch it. If you want to observe variables in the repo from the viewmodel, use
    // observeForever (not recommended as it's almost never needed)
    val allCityWeather: LiveData<List<WeatherTable>> = repository.allCityWeather.asLiveData()
    val allUserData: LiveData<List<UserTable>> = repository.allUser.asLiveData()

    //The singleton repository. If our app maps to one process, the recommended
    // pattern is to make repo and db singletons. That said, it's sometimes useful
    // to have more than one repo so it doesn't become a kitchen sink class, but each
    // of those repos could be singleton.
    private var mWeatherRepository: WeatherRepository = repository

    val currentPosition: Int
        get() = mWeatherRepository.current_position

    fun setLocation(location: String) {
        // Simply pass the location to the repository
        mWeatherRepository.setLocation(location)
    }

    fun setUserData(array_data: Array<String?>?) {
        mWeatherRepository.setUserData(array_data)
    }

    fun updateUserData(array_data: Array<String?>?) {
        mWeatherRepository.updateUserData(array_data)
    }

    fun updateUserData() {
        mWeatherRepository.updateUserData()
    }

    fun setCurrentPosition(position: Int) {
        mWeatherRepository.setCurrentPosition(position)
    }

    val userData: LiveData<UserData>
        get() = user_data

    // Returns the data contained in the live data object
    val data: LiveData<WeatherData>
        get() = jsonData
}

// This factory class allows us to define custom constructors for the view model
class WeatherViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
