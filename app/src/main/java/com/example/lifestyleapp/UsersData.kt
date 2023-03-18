package com.example.lifestyleapp

import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator
import java.util.ArrayList

class UsersData: Parcelable {
    private var mUserList: MutableList<String>? = null
    private var list_first_name: MutableList<String>? = null
    private var list_last_name: MutableList<String>? = null
    private var list_full_name: MutableList<String>? = null
    private var list_sex: MutableList<String>? = null
    private var list_weight: MutableList<String>? = null
    private var list_feet: MutableList<String>? = null
    private var list_inch: MutableList<String>? = null
    private var list_age: MutableList<String>? = null
    private var list_mbr: MutableList<String>? = null
    private var list_activity_level: MutableList<String>? = null
    private var list_calorie_intake: MutableList<String>? = null
    private var list_location: MutableList<String>? = null
    private var list_longitude: MutableList<String>? = null
    private var list_latitude: MutableList<String>? = null
    private var list_filepath: MutableList<String>? = null

    private constructor(`in`: Parcel) {
        `in`.readStringList(mUserList!!)
        `in`.readStringList(list_first_name!!)
        `in`.readStringList(list_last_name!!)
        `in`.readStringList(list_full_name!!)
        `in`.readStringList(list_sex!!)
        `in`.readStringList(list_weight!!)
        `in`.readStringList(list_feet!!)
        `in`.readStringList(list_inch!!)
        `in`.readStringList(list_age!!)
        `in`.readStringList(list_mbr!!)
        `in`.readStringList(list_activity_level!!)
        `in`.readStringList(list_calorie_intake!!)
        `in`.readStringList(list_location!!)
        `in`.readStringList(list_longitude!!)
        `in`.readStringList(list_latitude!!)
        `in`.readStringList(list_filepath!!)
    }

    constructor(numItems: Int) {
        //Populate the item list with data
        //and populate the details list with details at the same time
        mUserList = ArrayList()
        list_first_name = ArrayList()
        list_last_name = ArrayList()
        list_full_name = ArrayList()
        list_sex = ArrayList()
        list_weight = ArrayList()
        list_feet = ArrayList()
        list_inch = ArrayList()
        list_age = ArrayList()
        list_mbr = ArrayList()
        list_activity_level = ArrayList()
        list_calorie_intake = ArrayList()
        list_location = ArrayList()
        list_longitude = ArrayList()
        list_latitude = ArrayList()
        list_filepath = ArrayList()
        (mUserList as ArrayList<String>).add("New User")
    }

    //Say how and what to write to parcel
    override fun writeToParcel(out: Parcel, flags: Int) {
        out.writeStringList(mUserList)
        out.writeStringList(list_first_name)
        out.writeStringList(list_last_name)
        out.writeStringList(list_full_name)
        out.writeStringList(list_sex)
        out.writeStringList(list_weight)
        out.writeStringList(list_feet)
        out.writeStringList(list_inch)
        out.writeStringList(list_age)
        out.writeStringList(list_activity_level)
        out.writeStringList(list_calorie_intake)
        out.writeStringList(list_location)
        out.writeStringList(list_longitude)
        out.writeStringList(list_latitude)
        out.writeStringList(list_filepath)
    }

    //Don't worry about this for now.
    override fun describeContents(): Int {
        return 0
    }

    //Implement a getter and setter for getting whole list
    val itemList: MutableList<String>?
        get() = mUserList

    val picList: MutableList<String>?
        get() = list_filepath

    fun setItem(itemList: Array<String?>?) {
        itemList!![2]?.let { mUserList!!.add(it) }
        itemList[0]?.let { list_first_name!!.add(it) }
        itemList[1]?.let { list_last_name!!.add(it) }
        itemList[2]?.let { list_full_name!!.add(it) }
        itemList[3]?.let { list_sex!!.add(it) }
        itemList[4]?.let { list_weight!!.add(it) }
        itemList[5]?.let { list_feet!!.add(it) }
        itemList[6]?.let { list_inch!!.add(it) }
        itemList[7]?.let { list_age!!.add(it) }
        itemList[8]?.let { list_mbr!!.add(it) }
        itemList[9]?.let { list_activity_level!!.add(it) }
        itemList[10]?.let { list_calorie_intake!!.add(it) }
        itemList[11]?.let { list_location!!.add(it) }
        itemList[12]?.let { list_longitude!!.add(it) }
        itemList[13]?.let { list_latitude!!.add(it) }
        itemList[14]?.let { list_filepath!!.add(it) }
    }

    //Implement getter for item details at a position
    fun getItemDetail(position: Int): MutableList<String> {
        val list = mutableListOf<String>()
        list.add(list_first_name!![position])
        list.add(list_last_name!![position])
        list.add(list_full_name!![position])
        list.add(list_sex!![position])
        list.add(list_weight!![position])
        list.add(list_feet!![position])
        list.add(list_inch!![position])
        list.add(list_age!![position])
        list.add(list_mbr!![position])
        list.add(list_activity_level!![position])
        list.add(list_calorie_intake!![position])
        list.add(list_location!![position])
        list.add(list_longitude!![position])
        list.add(list_latitude!![position])
        list.add(list_filepath!![position])
        return list
    }

    companion object CREATOR : Creator<UsersData> {
        override fun createFromParcel(parcel: Parcel): UsersData {
            return UsersData(parcel)
        }

        override fun newArray(size: Int): Array<UsersData?> {
            return arrayOfNulls(size)
        }
    }
}