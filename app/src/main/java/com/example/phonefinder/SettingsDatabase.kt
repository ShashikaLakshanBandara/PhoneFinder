package com.example.phonefinder

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TableSettings::class], version = 1)
abstract class SettingsDatabase : RoomDatabase(){

    abstract fun SettingsDAO() : SettingsDAO

}