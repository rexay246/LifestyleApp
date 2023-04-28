package com.example.lifestyleapp

import androidx.room.*

import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // Insert ignore
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userTable: UserTable)

    // Delete all
    @Query("DELETE FROM user_table")
    suspend fun deleteAll()

    @Update
    suspend fun updateUser(userTable: UserTable)

    // Get all the weather info that is currently in the database
    // automatically triggered when the db is updated because of Flow<List<WeatherTable>>
    @Query("SELECT * from user_table where position!='0' ORDER BY position")
    fun getAllUser(): Flow<List<UserTable>>
}