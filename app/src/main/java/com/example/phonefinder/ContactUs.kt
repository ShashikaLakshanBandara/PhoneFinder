package com.example.phonefinder

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

class ContactUs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        val back = findViewById<Toolbar>(R.id.toolbar)

        val whatsAppButton: ImageView = findViewById(R.id.imageViewWhatsApp)
        val faceBookButton: ImageView = findViewById(R.id.imageViewFacebook)
        val gmailButton: ImageView = findViewById(R.id.imageViewGmail)

        gmailButton.setOnClickListener {
            val recipientEmail = "octochimphelp@gmail.com"
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
            startActivity(intent)
        }

        faceBookButton.setOnClickListener {
            val uri = Uri.parse("https://www.facebook.com/OctoChimp/")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        whatsAppButton.setOnClickListener {
            val uri = Uri.parse("https://wa.me/message/TH5LFW2MORAEE1")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        back.setOnClickListener {
            this.finish()
        }
    }
}