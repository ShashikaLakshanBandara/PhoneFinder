package com.example.phonefinder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MyBroadcastReceiver : BroadcastReceiver() {



    override fun onReceive(context: Context?, intent: Intent?) {

        Toast.makeText(context, "Function Activated", Toast.LENGTH_SHORT).show()
        val telephonyManager = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val newSimState = telephonyManager.simState

        CoroutineScope(Dispatchers.IO).launch {

            val appDatabase = Room.databaseBuilder(context, SettingsDatabase::class.java, "settings").build()
            val settingsDao = appDatabase.SettingsDAO()
            val settings = settingsDao.getSettingsById(1)

            withContext(Dispatchers.Main) {
                settings?.let {
                    val phoneNumber = it.backupPhone
                    sendReplyMessage(context,phoneNumber,"This message from new number!")
                }
            }
        }


    }
    private fun sendReplyMessage(context: Context, sender: String, message: String) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(sender, null, message, null, null)
    }
}
