package com.example.phonefinder

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // Launch a new coroutine to pause execution
        GlobalScope.launch {
            delay(10000) // Pause execution for 10 seconds

            // Resume execution after the delay
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Function Activated", Toast.LENGTH_SHORT).show()
                val telephonyManager = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val newSimState = telephonyManager.simState

                // Check if the app has SEND_SMS permission
                if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with sending SMS
                    sendSMS(context)
                } else {
                    // Permission not granted
                    Log.e("shashika", "SEND_SMS permission not granted")
                }
            }
        }
    }

    private fun sendSMS(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("shashika", "In CoroutineScope")
            val appDatabase = Room.databaseBuilder(context, SettingsDatabase::class.java, "settings").build()
            val settingsDao = appDatabase.SettingsDAO()
            val settings = settingsDao.getSettingsById(1)

            withContext(Dispatchers.Main) {
                settings?.let {
                    val phoneNumber = it.backupPhone
                    sendReplyMessage(context, phoneNumber, "This message from new number!")
                }
            }
        }
    }

    private fun sendReplyMessage(context: Context, sender: String, message: String) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(sender, null, message, null, null)
    }
}
