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
class MainActivity : AppCompatActivity(), InputFragment.SendDataInterface {
    private var first_name: String? = null
    private var last_name: String? = null
    private var mbr: String? = null
    private var calorie_intake: String? = null
    private var filepath: String? = null

    private var inputFragment: InputFragment? = null
    private var displayFragment: DisplayFragment? = null
    private var checkInputFragment: Boolean? = null
    private var checkDisplayFragment: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            checkInputFragment = true
            checkDisplayFragment = false
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

        if (checkInputFragment == true) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_holder, inputFragment!!, "input_fragment")
            transaction.addToBackStack(null)
            transaction.commit()
        }
        else if (checkDisplayFragment == true) {
            createDisplayFragment()
        }
    }

    override fun sendData(data: Array<String?>?) {
        first_name = data!![0]
        last_name = data[1]
        mbr = data[2]
        calorie_intake = data[3]
        filepath = data[4]

        displayFragment = DisplayFragment()
        createDisplayFragment()

        checkDisplayFragment = true
        checkInputFragment = false
    }

    fun createDisplayFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        val sendData = Bundle()
        sendData.putString("first_name", first_name)
        sendData.putString("last_name", last_name)
        sendData.putString("filepath", filepath)
        sendData.putString("bmr_data", mbr)
        sendData.putString("activity_data", calorie_intake)
        displayFragment!!.arguments = sendData

        transaction.replace(R.id.fragment_holder, displayFragment!!, "display_fragment")
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("display_data", checkDisplayFragment!!)
        outState.putBoolean("input_data", checkInputFragment!!)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        checkInputFragment = savedInstanceState.getBoolean("input_data")
        checkDisplayFragment = savedInstanceState.getBoolean("display_data")

        super.onRestoreInstanceState(savedInstanceState)
    }
}