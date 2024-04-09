package com.example.phonefinder

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class TableSettings(
    @PrimaryKey val id:Int,
    @ColumnInfo(name = "user_name") val userName : String,
    @ColumnInfo(name = "email_address")val emailAddress : String,
    @ColumnInfo(name = "security_code")val securityCode : String,
    @ColumnInfo(name = "backup_phone")val backupPhone : String,
    @ColumnInfo(name = "propic_path")val propicPath : String

)
