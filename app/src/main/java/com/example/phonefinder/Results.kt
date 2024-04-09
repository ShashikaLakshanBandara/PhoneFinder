package com.example.phonefinder

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toolbar

class Results : AppCompatActivity() {

    private lateinit var smsReceiver: SMSReceiver

    // Retrieve the variables sent from FindActivity
    var BS: String? = null
    var WS: String? = null
    var GS: String? = null
    var DS: String? = null
    var PS: String? = null
    var VS: String? = null
    var AS: String? = null
    var FS: String? = null

    var phoneNumber: String? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        smsReceiver = SMSReceiver()

        // Register SMSReceiver to receive SMS_RECEIVED_ACTION broadcasts
        val intentFilter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        registerReceiver(smsReceiver, intentFilter)

        BS = intent.getStringExtra("BS")
        WS = intent.getStringExtra("WS")
        GS = intent.getStringExtra("GS")
        DS = intent.getStringExtra("DS")
        PS = intent.getStringExtra("PS")
        VS = intent.getStringExtra("VS")
        AS = intent.getStringExtra("AS")
        FS = intent.getStringExtra("FS")
        phoneNumber = intent.getStringExtra("phoneNumber")

        val btnRemoteMode = findViewById<Button>(R.id.btnRemoteMode)
        val back = findViewById<Toolbar>(R.id.toolbarBack)

        val textViewBS: TextView = findViewById(R.id.TVpercentage)
        val textViewWS: TextView = findViewById(R.id.TVwifistatus)
        val textViewGS: TextView = findViewById(R.id.TVgpsStatus)
        val textViewDS: TextView = findViewById(R.id.TVmobileDataStatus)
        val textViewPS: TextView = findViewById(R.id.TVprofileModeStatus)
        val textViewVS: TextView = findViewById(R.id.TVvolumeStatus)
        val textViewAS: TextView = findViewById(R.id.TVautoSyncStatus)




        Log.d("shashika","Values in Results ")
        Log.d("shashika", "BS = $BS")
        Log.d("shashika", "WS = $WS")
        Log.d("shashika", "GS = $GS")
        Log.d("shashika", "DS = $DS")
        Log.d("shashika", "PS = $PS")
        Log.d("shashika", "VS = $VS")
        Log.d("shashika", "AS = $AS")
        Log.d("shashika", "FS = $FS")
        Log.d("shashika", "phone Number = $phoneNumber")

        textViewBS.text = BS.toString()
        textViewWS.text = WS.toString()
        textViewGS.text = GS.toString()
        textViewDS.text = DS.toString()
        textViewPS.text = PS.toString()
        textViewVS.text = VS.toString()
        textViewAS.text = AS.toString()



        back.setOnClickListener {
            this.finish()
        }


        btnRemoteMode.setOnClickListener {
            val intent = Intent(this,RemoteMode::class.java)

            // Pass the variables to the RemoteMode activity
            intent?.putExtra("BS", BS)
            intent?.putExtra("WS", WS)
            intent?.putExtra("GS", GS)
            intent?.putExtra("DS", DS)
            intent?.putExtra("PS", PS)
            intent?.putExtra("VS", VS)
            intent?.putExtra("AS", AS)
            intent?.putExtra("FS", FS)
            intent?.putExtra("phoneNumber", phoneNumber)

            startActivity(intent)
        }





    }
}