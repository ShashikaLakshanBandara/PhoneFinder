package com.example.phonefinder

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Settings : AppCompatActivity() {

    private var mInterstitialAd: InterstitialAd? = null
    private final val TAG = "Settings"

    private var initialSecurityCode = ""

    private lateinit var ETuserName: EditText
    private lateinit var ETemailAddress: EditText
    private lateinit var ETsecurityCode: EditText
    private lateinit var ETvsecurityCode: EditText
    private lateinit var ETbackupPhoneNumber: EditText
    private lateinit var btnImageSelect : FloatingActionButton
    private lateinit var AvatarImage: ImageView
    var switch = 1
    var resourceName = "profile_pic"


    private lateinit var db: SettingsDatabase





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        MobileAds.initialize(this) {}

        ETuserName = findViewById<EditText>(R.id.ETuserName)
        ETemailAddress = findViewById<EditText>(R.id.ETemailAddress)
        ETsecurityCode = findViewById<EditText>(R.id.ETsecurityCode)
        ETvsecurityCode = findViewById<EditText>(R.id.ETconfirmSecurityCode)
        ETbackupPhoneNumber = findViewById<EditText>(R.id.ETbackupPhoneNumber)
        btnImageSelect = findViewById(R.id.floatingActionButton)
        AvatarImage = findViewById(R.id.imageView3)

        val btnSave = findViewById<Button>(R.id.btnSave)


        val db = Room.databaseBuilder(
            applicationContext,
            SettingsDatabase::class.java,"settings"
        ).build()

        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError?.toString().toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })

        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }

        // Use lifecycleScope to launch a coroutine
        lifecycleScope.launch {
            // Perform database operations in a background thread
            val settings = withContext(Dispatchers.IO) {
                db.SettingsDAO().getSettingsById(1)
            }

            try {
                ETuserName.setText(settings.userName)
                ETemailAddress.setText(settings.emailAddress)
                ETsecurityCode.setText(settings.securityCode)
                ETbackupPhoneNumber.setText(settings.backupPhone)
                resourceName = settings.propicPath
                var resourceId = resources.getIdentifier(resourceName,"drawable",packageName)
                AvatarImage.setImageResource(resourceId)
                initialSecurityCode = (settings.securityCode)
            }
            catch (e:Exception){
                ETuserName.setText("")
                ETemailAddress.setText("")
                ETsecurityCode.setText("XXXX")
                ETvsecurityCode.setText("")
                ETbackupPhoneNumber.setText("")
                AvatarImage.setImageResource(R.drawable.profile_pic)

            }


        }


        //back button
        val back = findViewById<Toolbar>(R.id.toolbar)
        back.setOnClickListener {

            this.finish()
        }

        btnSave.setOnClickListener {

            val userNameInserted = ETuserName.text
            val emailAddressInserted = ETemailAddress.text
            val securityCodeInserted = ETsecurityCode.text
            val backupPhoneInserted = ETbackupPhoneNumber.text

            var validationString = checkValidation(userNameInserted.toString(), emailAddressInserted.toString(), securityCodeInserted.toString(), backupPhoneInserted.toString())



            when{
                validationString == true -> {
                    val newSettings = TableSettings(
                        1,
                        userNameInserted.toString(),
                        emailAddressInserted.toString(),
                        securityCodeInserted.toString(),
                        backupPhoneInserted.toString(),
                        resourceName
                    )

                    lifecycleScope.launch {

                        try {
                            db.SettingsDAO().insertSettings(newSettings)
                            Toast.makeText(this@Settings, "All Saved!", Toast.LENGTH_SHORT).show()
                        }
                        catch (e: Exception){
                            db.SettingsDAO().updateSettings(newSettings)
                            Toast.makeText(this@Settings, "All Updated!", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
                else -> {

                }
            }
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }


        }

        btnImageSelect.setOnClickListener {
            if(switch>=8){
                switch = 0
            }
            val imageList: Array<String> = arrayOf("profile_pic","profile_pic2","profile_pic3","profile_pic4","profile_pic5","profile_pic6","profile_pic7","profile_pic8")
            resourceName = imageList[switch]
            var resourceId = resources.getIdentifier(resourceName,"drawable",packageName)
            AvatarImage.setImageResource(resourceId)
            switch++

        }




    }

    private fun checkValidation(name: String, email: String, code: String, phone: String): Any {
        Log.d("shashika","checkValidation")

        when(name) {
            "" -> {
                Toast.makeText(this,"Name should not be empty!",Toast.LENGTH_SHORT).show()
                return false
            }
        }
        when(email) {
            "" -> {
                Toast.makeText(this,"Email Address should not be empty!",Toast.LENGTH_SHORT).show()
                return false
            }
        }
        when(phone) {
            "" -> {
                Toast.makeText(this,"Phone Number should not be empty!",Toast.LENGTH_SHORT).show()
                return false
            }
        }
        Log.d("shashika","initialSecurityCode = $initialSecurityCode ETsecurityCode = ${ETsecurityCode.text}")
        if(initialSecurityCode != ETsecurityCode.text.toString()) {
            Log.d("shashika","password changed!")
            if(ETvsecurityCode.text.toString().isEmpty()){
                Toast.makeText(this,"Re enter security code!",Toast.LENGTH_SHORT).show()
                return false
            }
            else if(ETsecurityCode.text.toString() != ETvsecurityCode.text.toString()){
                Toast.makeText(this,"Codes are not matching!",Toast.LENGTH_SHORT).show()
            }
            else{
                return true
            }

        }

        return true

    }


}