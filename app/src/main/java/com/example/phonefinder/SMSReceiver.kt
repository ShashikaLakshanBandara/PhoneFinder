package com.example.phonefinder

import android.Manifest
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Bundle
import android.provider.Settings
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SMSReceiver : BroadcastReceiver() {

    private lateinit var db: SettingsDatabase
    private var smsNumber: String? = null

    // Declare mediaPlayer as a class-level variable
    private var mediaPlayer: MediaPlayer? = null

    private var locationManager: LocationManager? = null
    private var currentLocation: Location? = null

    // Define a flag to track playback status
    private var isPlaying: Boolean = false


    private fun checkCommand(smsBody: String, context: Context?) {
        Log.d("shashika", "In checkCommand function")

        val commandFrom = smsBody.substringBefore("\n")
        val command = smsBody.substringAfter("Command:").substringBefore("\n")
        val securityCodeReceived = smsBody.substringAfter("Code:\"").substringBefore("\"")

        val db = Room.databaseBuilder(
            context!!.applicationContext,
            SettingsDatabase::class.java, "settings"
        ).build()

        CoroutineScope(Dispatchers.IO).launch {
            // Perform database operations in the
            val settings = db.SettingsDAO().getSettingsById(1)
            val securityCodeDatabase = settings.securityCode

            // Switch to the main thread before displaying Toast messages
            withContext(Dispatchers.Main) {

                Log.d("shashika", "Commad is - $command")
                Log.d("shashika", "Security Code is - $securityCodeReceived")
                //Log.d("shashika", "$securityCodeReceived = $securityCodeDatabase")

                Log.d("shashika", "CommandFromPhoneFinder == $commandFrom && CHECK_VERIFICATION == $command && $securityCodeReceived == $securityCodeDatabase")
                Log.d("shashika", "CommandFromPhoneFinder == $commandFrom && RESPONSE_MESSAGE == $command")
                if (commandFrom == "CommandFromPhoneFinder" && command == "CHECK_VERIFICATION" && securityCodeReceived == securityCodeDatabase) {
                    Toast.makeText(context, "Codes are matching!", Toast.LENGTH_SHORT).show()
                    Log.d("shashika", "In the CHECK_VERIFICATION")
                    var batteryStatus = getBatteryPercentage(context)
                    var wifiStatus = getWifiStatus(context)
                    var GPSStatus = getGpsStatus(context)
                    var mobileDataStatus = getMobileDataOnOff(context)
                    var profileStatus = getProfileStatus(context)
                    var volumeStatus = getPhoneVolumeLevel(context)
                    var autoSyncStatus = getAutoSyncLevel(context)

                    Log.d("shashika", "batteryStatus = $batteryStatus")
                    Log.d("shashika", "wifiStatus = $wifiStatus")
                    Log.d("shashika", "GPSStatus = $GPSStatus")
                    Log.d("shashika", "mobileDataStatus = $mobileDataStatus")
                    Log.d("shashika", "profileStatus = $profileStatus")
                    Log.d("shashika", "volumeStatus = $volumeStatus")
                    Log.d("shashika", "autoSyncStatus = $autoSyncStatus")

                    var responseMessage = "CommandFromPhoneFinder" +
                            "\nCommand:RESPONSE_MESSAGE" +
                            "\nBS:\"$batteryStatus\"" +
                            "\nWS:\"$wifiStatus\"" +
                            "\nGS:\"$GPSStatus\"" +
                            "\nDS:\"$mobileDataStatus\"" +
                            "\nPS:\"$profileStatus\"" +
                            "\nVS:\"$volumeStatus\"" +
                            "\nAS:\"$autoSyncStatus\"" +
                            "\nFS:\"OFF\""

                    sendMessage(smsNumber, responseMessage, context)


                }
                else if(commandFrom == "CommandFromPhoneFinder" && command == "RESPONSE_MESSAGE"){
                    Log.d("shashika", "In the RESPONSE_MESSAGE")

                    // Extract the variables you want to send

                    val BS = smsBody.substringAfter("BS:\"").substringBefore("\"")
                    val WS = smsBody.substringAfter("WS:\"").substringBefore("\"")
                    val GS = smsBody.substringAfter("GS:\"").substringBefore("\"")
                    val DS = smsBody.substringAfter("DS:\"").substringBefore("\"")
                    val PS = smsBody.substringAfter("PS:\"").substringBefore("\"")
                    val VS = smsBody.substringAfter("VS:\"").substringBefore("\"")
                    val AS = smsBody.substringAfter("AS:\"").substringBefore("\"")
                    val FS = smsBody.substringAfter("FS:\"").substringBefore("\"")




                    // Send a broadcast indicating that the verification is successful
                    val verifiedIntent = Intent("com.example.phonefinder.VERIFIED_RESPONSE")

                    Log.d("shashika","Values in SMSReceiver ")
                    Log.d("shashika", "BS = $BS")
                    Log.d("shashika", "WS = $WS")
                    Log.d("shashika", "GS = $GS")
                    Log.d("shashika", "DS = $DS")
                    Log.d("shashika", "PS = $PS")
                    Log.d("shashika", "VS = $VS")
                    Log.d("shashika", "AS = $AS")
                    Log.d("shashika", "FS = $AS")
                    Log.d("shashika", "phone Number = $smsNumber")


                    verifiedIntent.putExtra("BS", BS)
                    verifiedIntent.putExtra("WS", WS)
                    verifiedIntent.putExtra("GS", GS)
                    verifiedIntent.putExtra("DS", DS)
                    verifiedIntent.putExtra("PS", PS)
                    verifiedIntent.putExtra("VS", VS)
                    verifiedIntent.putExtra("AS", AS)
                    verifiedIntent.putExtra("FS", FS)
                    verifiedIntent.putExtra("phoneNumber", smsNumber)
                    context?.sendBroadcast(verifiedIntent)
                }
                else if(commandFrom == "CommandFromPhoneFinder" && command == "FROM_REOMTEMODE") {
                    Log.d("shashika", "In the FROM_REOMTEMODE")
                    var remoteCmdFS = smsBody.substringAfter("FLASH:\"").substringBefore("\"")
                    var remoteCmdMS = smsBody.substringAfter("VOL:\"").substringBefore("\"")

                    when(remoteCmdFS) {

                        "ON" -> turnOnFlashlight(context)
                        "OFF" -> turnOffFlashlight(context)

                    }
                    when(remoteCmdMS) {
                        "ON" -> turnOnMaxVolume(context)
                    }



                }
                else if(commandFrom == "CommandFromPhoneFinder" && command == "FROM_REOMTEMODEPLAYSOUND") {
                    var playSound = smsBody.substringAfter("Play:\"").substringBefore("\"")
                    when(playSound){
                        "ON" -> playRingingTone(context)
                        "OFF" -> stopRingingTone(context)
                    }
                }
                else if(commandFrom == "CommandFromPhoneFinder" && command == "FROM_REOMTEMODE_GETLOCATION") {
                    getCurrentLocation(context,smsNumber)
                }
                else if(commandFrom == "CommandFromPhoneFinder" && command == "CURRENTLOCATION") {
                    var sms = smsBody.substringAfter("Location:\"").substringBefore("\"")
                    (context as? RemoteMode)?.displayLocation(sms)
                }
                else {
                    Log.d("shashika", "else")
                }
            }
        }
    }
    private fun getCurrentLocation(context: Context, Sender : String? ) {
        Log.d("shashika", "in getCurrentLocation")


        // Create location manager
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        Log.d("shashika", "in locationManager")

        // Define location listener
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Location update received
                currentLocation = location
                if (currentLocation != null) {
                    Log.d("shashika", "In the location if")
                    val latitude = currentLocation!!.latitude
                    val longitude = currentLocation!!.longitude

                    val message = "Current Location: \nLat=$latitude,\nLng=$longitude\nLocationReply"
                    Log.d("shashika", message)

                    var msg = "CommandFromPhoneFinder" +
                            "\nCommand:CURRENTLOCATION" +
                            "\nLocation:\"$message\""
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    sendMessage(smsNumber,msg,context)
                } else {
                    Log.d("shashika", "In the location else")
                    Toast.makeText(context, "Failed to retrieve current location", Toast.LENGTH_SHORT).show()
                }

                // Remember to remove the location updates if you no longer need them
                locationManager?.removeUpdates(this)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {
                sendMessage(smsNumber,"GPS not enabled or not working correctly!",context)
            }

        }

        // Request location updates
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the missing permissions if needed.
            // See the documentation for ActivityCompat.requestPermissions for more details.
            return
        }
        locationManager?.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null)

    }
    // Function to play the default ringing tone continuously
    /*private fun playRingingTone(context: Context) {
        //Toast.makeText(context, "sound = $sound", Toast.LENGTH_SHORT).show()
        // Check if the mediaPlayer is already playing
        if (mediaPlayer?.isPlaying == true) {
            // If already playing, stop it first before starting again
            Toast.makeText(context, " If already playing, stop it first before starting again", Toast.LENGTH_SHORT).show()
            mediaPlayer?.stop()
            mediaPlayer?.reset()
        } else {
            val ringtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

            // Create a new instance of MediaPlayer
            mediaPlayer = MediaPlayer()

            // Set audio attributes for the MediaPlayer
            mediaPlayer?.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )

            // Set the ringtone URI to the MediaPlayer
            mediaPlayer?.setDataSource(context, ringtoneUri)
            // Enable looping to play the ringtone continuously
            mediaPlayer?.isLooping = true
            // Prepare the MediaPlayer asynchronously
            mediaPlayer?.prepareAsync()

            // Start playing the system default ringtone
            mediaPlayer?.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.start()
            }
        }
    }*/
    // Update the flag when starting or stopping playback
    fun playRingingTone(context: Context){
        Log.d("shashika","playRingingTone")
        if (mediaPlayer == null) {
            Log.d("shashika","media player null")
            mediaPlayer = MediaPlayer.create(context, R.raw.sound)
            mediaPlayer?.isLooping = true
            isPlaying = true // Update the flag
        }

        mediaPlayer?.start()
    }

    fun stopRingingTone(context: Context){
        Log.d("shashika","stopRingingTone")
        try {
            if (isPlaying) { // Check the flag instead of mediaPlayer status
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                isPlaying = false // Update the flag
                Log.d("shashika","media player stopped and released")
            } else {
                Log.d("shashika","media player is not playing")
            }
        } catch (e: Exception) {
            Log.e("shashika", "Error stopping MediaPlayer: ${e.message}")
        }
    }


    // Function to stop the ringing tone
    /*private fun stopRingingTone(context: Context) {
        // Check if the mediaPlayer is currently playing
        // Stop the mediaPlayer if it's currently playing
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
        }
        refreshSMSReceiver(context)


    }*/
    fun refreshSMSReceiver(context: Context) {
        Log.d("shashika", "Refreshing")
        // Unregister the existing receiver
        try {
            val receiver = SMSReceiver()
            context.unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            // Receiver may not have been registered yet or is already unregistered
            e.printStackTrace()
        }

        // Register the receiver again
        val intentFilter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        context.registerReceiver(SMSReceiver(), intentFilter)
    }

    fun turnOnFlashlight(context: Context) {
        Log.d("shashika", "In the turnOnFlashLightFunction")
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, true)
            sendMessage(smsNumber,"Flash light is enabled!",context)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            sendMessage(smsNumber,e.toString(),context)
        }
    }
    fun turnOffFlashlight(context: Context) {
        Log.d("shashika", "In the turnOFFFlashLightFunction")
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, false)
            sendMessage(smsNumber,"Flash light is disabled!",context)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            sendMessage(smsNumber,e.toString(),context)
        }
    }
    private fun turnOnMaxVolume(context: Context) {
        Log.d("shashika", "In the turnOnMaxVolume")
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            // Check sound mode
            val soundMode = audioManager.ringerMode

            // Change to sound mode if in silent or vibration mode
            if (soundMode == AudioManager.RINGER_MODE_SILENT || soundMode == AudioManager.RINGER_MODE_VIBRATE) {
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            }

            // Set all volume levels to max
            //audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
            //audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), 0)
            //audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM), 0)

            Toast.makeText(context, "Sound Max", Toast.LENGTH_SHORT).show()
        }
        catch(e: Exception){
            sendMessage(smsNumber,"The phone is in Do not disturb mode. This feature only allowed when phone is in vibrate mode",context)
        }

    }

    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("SMSReceiver", "SMS Received")




        Log.d("shashika", "New message received")
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent?.action) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (sms in messages) {
                val smsBody = sms.messageBody
                smsNumber = sms.originatingAddress

                Toast.makeText(context, smsBody, Toast.LENGTH_SHORT).show()
                try {
                    if ("CommandFromPhoneFinder" == smsBody.substringBefore("\n")) {
                        checkCommand(smsBody, context)
                    }
                } catch (e: Exception) {


                    e.printStackTrace()
                }
            }
        }
    }

    fun getBatteryPercentage(context: Context): String {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

        val batteryPct: Float = level / scale.toFloat()
        return (batteryPct * 100).toInt().toString() + "%"
    }

    fun getWifiStatus(context: Context): String {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return if (wifiManager.isWifiEnabled) "ON" else "OFF"
    }

    fun getGpsStatus(context: Context): String {
        val isLocationEnabled = Settings.Secure.getInt(
            context.contentResolver,
            Settings.Secure.LOCATION_MODE,
            Settings.Secure.LOCATION_MODE_OFF
        )
        return if (isLocationEnabled != Settings.Secure.LOCATION_MODE_OFF) "ON" else "OFF"
    }

    fun getMobileDataOnOff(context: Context): String {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mobileDataEnabled: Boolean
        mobileDataEnabled = try {
            val cmClass = Class.forName(connectivityManager.javaClass.name)
            val method = cmClass.getDeclaredMethod("getMobileDataEnabled")
            method.isAccessible = true
            method.invoke(connectivityManager) as Boolean
        } catch (e: Exception) {
            Log.e("Error", e.toString())
            false
        }
        return if (mobileDataEnabled) "ON" else "OFF"
    }

    fun getProfileStatus(context: Context): String {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_SILENT -> "Silent"
            AudioManager.RINGER_MODE_VIBRATE -> "Vibrate"
            AudioManager.RINGER_MODE_NORMAL -> "Normal"
            else -> "Unknown"
        }
    }

    fun getPhoneVolumeLevel(context: Context): String {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)

        val volumePercentage = (currentVolume.toFloat() / maxVolume.toFloat() * 100).toInt()

        return "$volumePercentage%"
    }

    fun getAutoSyncLevel(context: Context): String {
        val contentResolver = context.contentResolver
        return if (ContentResolver.getMasterSyncAutomatically()) {
            "ON"
        } else {
            "OFF"
        }
    }

    private fun sendMessage(phoneNumber: String?, message: String, context: Context?) {
        Log.d("shashika", "Response Send!")
        Log.d("shashika", "phoneNumber = $phoneNumber")
        Log.d("shashika", "message  = $message")

        val smsManager = SmsManager.getDefault()

        try{
            // Check if the message length exceeds the maximum SMS length
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Log.d("shashika","message sent!")
        }
        catch(e: Exception) {
            Log.d("shashika","$e")
        }

    }




}
