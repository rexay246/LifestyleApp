package com.example.lifestyleapp

import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "user_table")
data class UserTable(
    @field:ColumnInfo(name = "position")
    @field:PrimaryKey
    var position: Int,
    @field:ColumnInfo(name = "user_name") var user_name: String,
    @field:ColumnInfo(name = "last_name") var last_name: String,
    @field:ColumnInfo(name = "full_name") var full_name: String,
    @field:ColumnInfo(name = "sex") var sex: String,
    @field:ColumnInfo(name = "weight") var weight: String,
    @field:ColumnInfo(name = "feet") var feet: String,
    @field:ColumnInfo(name = "inch") var inch: String,
    @field:ColumnInfo(name = "age") var age: String,
    @field:ColumnInfo(name = "mbr") var mbr: String,
    @field:ColumnInfo(name = "activity_level") var activity_level: String,
    @field:ColumnInfo(name = "calorie_intake") var calorie_intake: String,
    @field:ColumnInfo(name = "location") var location: String,
    @field:ColumnInfo(name = "longitude") var longitude: String,
    @field:ColumnInfo(name = "latitude") var latitude: String,
    @field:ColumnInfo(name = "filename") var filename: String,
)