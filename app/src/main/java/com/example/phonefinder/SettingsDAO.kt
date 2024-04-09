package com.example.phonefinder

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SettingsDAO {

    @Query("SELECT * FROM settings WHERE id = :id")
    fun getSettingsById(id:Int) : TableSettings

    @Insert
    suspend fun insertSettings(settings: TableSettings)

    @Update
    suspend fun updateSettings(settings: TableSettings)


}