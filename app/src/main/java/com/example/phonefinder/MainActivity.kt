package com.example.phonefinder

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val iv_note = findViewById<ImageView>(R.id.iv_note)
        val tv_splash = findViewById<TextView>(R.id.tv_splash_screen)

        iv_note.alpha = 0f
        iv_note.animate().setDuration(3000).alpha(1f).withEndAction {
            val intent = Intent(this, StartDisplay::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
        tv_splash.alpha = 0f
        tv_splash.animate().setDuration(3000).alpha(1f).withEndAction {
            val intent = Intent(this, StartDisplay::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }


}