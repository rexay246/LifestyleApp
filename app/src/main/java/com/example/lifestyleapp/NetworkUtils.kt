package com.example.lifestyleapp

import androidx.annotation.WorkerThread
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.Throws

object NetworkUtils {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q="
    private const val APPIDQUERY = "&appid="
    private const val app_id = "f36b0d473419f1af3f06e013ee376e00"
    fun buildURLFromString(location: String): URL? {
        var myURL: URL? = null
        try {
            myURL = URL(BASE_URL + location + APPIDQUERY + app_id)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return myURL
    }

    @WorkerThread
    fun getDataFromURL(url: URL): String? {
        val urlConnection = url.openConnection() as HttpURLConnection
        return try {
            val inputStream = urlConnection.inputStream

            //The scanner trick: search for the next "beginning" of the input stream
            //No need to user BufferedReader
            val scanner = Scanner(inputStream)
            scanner.useDelimiter("\\A")
            val hasInput = scanner.hasNext()
            if (hasInput) {
                scanner.next()
            } else {
                null
            }
        } finally {
            urlConnection.disconnect()
        }
    }
}