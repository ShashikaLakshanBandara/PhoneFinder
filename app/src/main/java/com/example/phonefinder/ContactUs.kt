package com.example.phonefinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar

class ContactUs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        val back = findViewById<Toolbar>(R.id.toolbar)

        back.setOnClickListener {
            this.finish()
        }
    }
}