package com.example.phonefinder

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsManager
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toolbar

class RemoteMode : AppCompatActivity() {

    private lateinit var smsReceiver: SMSReceiver


    // Declare variables without initialization
    private var BS: String? = null
    private var WS: String? = null
    private var GS: String? = null
    private var DS: String? = null
    private var PS: String? = null
    private var VS: String? = null
    private var AS: String? = null
    private var FS: String? = null
    var phoneNumber: String? = null

    private lateinit var switchFL: Switch
    private lateinit var switchMS: Switch
    private lateinit var switchPS: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote_mode)

        smsReceiver = SMSReceiver()

        // Register SMSReceiver to receive SMS_RECEIVED_ACTION broadcasts
        val intentFilter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        registerReceiver(smsReceiver, intentFilter)

        // Retrieve the intent extras within onCreate
        BS = intent.getStringExtra("BS")
        WS = intent.getStringExtra("WS")
        GS = intent.getStringExtra("GS")
        DS = intent.getStringExtra("DS")
        PS = intent.getStringExtra("PS")
        VS = intent.getStringExtra("VS")
        AS = intent.getStringExtra("AS")
        FS = intent.getStringExtra("FS")
        phoneNumber = intent.getStringExtra("phoneNumber")

        Log.d("shashika","Values in RemoteMOde ")
        Log.d("shashika", "BS = $BS")
        Log.d("shashika", "WS = $WS")
        Log.d("shashika", "GS = $GS")
        Log.d("shashika", "DS = $DS")
        Log.d("shashika", "PS = $PS")
        Log.d("shashika", "VS = $VS")
        Log.d("shashika", "AS = $AS")
        Log.d("shashika", "FS = $FS")
        Log.d("shashika", "phone Number = $phoneNumber")

        val back = findViewById<Toolbar>(R.id.toolbar)
        val btnProceed = findViewById<Button>(R.id.btnProceed)
        val btnGetLocation = findViewById<Button>(R.id.btnGrab)
        val btnViewLocation = findViewById<Button>(R.id.btnView)




        // Initialize Switch variables with their respective views
        switchFL = findViewById(R.id.switchFlash)
        switchMS = findViewById(R.id.switchSound)
        switchPS = findViewById(R.id.switchPlaySound)

        LoadCurrentStatus()

        back.setOnClickListener {
            this.finish()
        }
        btnProceed.setOnClickListener {
            Log.d("shashika","Clicked proceed button")
            when(switchFL.isChecked){
                true -> FS = "ON"
                else -> FS = "OFF"
            }
            when(switchMS.isChecked){
                true -> {
                    VS = "ON"
                    switchMS.isEnabled = false
                }
                else -> VS = "OFF"
            }

            var msg = "CommandFromPhoneFinder" +
            "\nCommand:FROM_REOMTEMODE" +
                    "\nFLASH:\"$FS\"" +
                    "\nVOL:\"$VS\""

            sendMessage(phoneNumber,msg)

        }
        btnViewLocation.setOnClickListener {
            val ETLocation = findViewById<EditText>(R.id.ETlocation)

            openLocationInGoogleMaps(ETLocation.text.toString())
        }
        btnGetLocation.setOnClickListener {
            var msg = "CommandFromPhoneFinder" +
                    "\nCommand:FROM_REOMTEMODE_GETLOCATION"
            sendMessage(phoneNumber,msg)
        }
        switchMS.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Change thumb color when checked
                switchMS.thumbTintList = ColorStateList.valueOf(resources.getColor(R.color.switchOn))
            } else {
                // Change thumb color when unchecked
                switchMS.thumbTintList = ColorStateList.valueOf(resources.getColor(R.color.switchOff))
            }
        }
        switchFL.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Change thumb color when checked
                switchFL.thumbTintList = ColorStateList.valueOf(resources.getColor(R.color.switchOn))
            } else {
                // Change thumb color when unchecked
                switchFL.thumbTintList = ColorStateList.valueOf(resources.getColor(R.color.switchOff))
            }
        }
        switchPS.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Change thumb color when checked
                switchPS.thumbTintList = ColorStateList.valueOf(resources.getColor(R.color.switchOn))
                var msg = "CommandFromPhoneFinder" +
                        "\nCommand:FROM_REOMTEMODEPLAYSOUND" +
                        "\nPlay:\"ON\""
                sendMessage(phoneNumber,msg)


            } else {
                // Change thumb color when unchecked
                switchPS.thumbTintList = ColorStateList.valueOf(resources.getColor(R.color.switchOff))
                var msg = "CommandFromPhoneFinder" +
                        "\nCommand:FROM_REOMTEMODEPLAYSOUND" +
                        "\nPlay:\"OFF\""
                sendMessage(phoneNumber,msg)
            }
        }
    }
    fun displayLocation(location: String) {
        val tfLocation = findViewById<TextView>(R.id.ETlocation)
        val editableText: Editable = Editable.Factory.getInstance().newEditable(location)
        tfLocation.text = editableText
    }


    private fun sendMessage(phoneNumber: String?, message: String) {
        Log.d("shashika", "Response Send!")
        Log.d("shashika", "phoneNumber = $phoneNumber")
        Log.d("shashika", "message  = $message")

        val smsManager = SmsManager.getDefault()

        smsManager.sendTextMessage(phoneNumber, null, message, null, null)


    }
    fun openLocationInGoogleMaps(locationString: String) {
        Log.d("shashika","Location = $locationString")
        val startIndex = locationString.indexOf("Lat=")
        val endIndex = locationString.indexOf(",", startIndex)
        val latitude = locationString.substring(startIndex + 4, endIndex).toDouble()

        val startIndexLng = locationString.indexOf("Lng=", endIndex)
        val endIndexLng = locationString.indexOf("\n", startIndexLng)
        val longitude = locationString.substring(startIndexLng + 4, endIndexLng).toDouble()

        val uri = Uri.parse("geo:$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")

        // Check if the Google Maps app is installed
        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            // Google Maps app is not installed, open in web browser
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"))
            startActivity(webIntent)
        }
    }

    private fun LoadCurrentStatus() {
        Log.d("shashika","VS = $VS")
        when(VS) {
            "100%" -> switchMS.isChecked = true
            else -> switchMS.isChecked = false
        }
        when(FS) {
            "ON" -> switchFL.isChecked = true
            else -> switchFL.isChecked = false
        }
        switchPS.isChecked = false

    }
}