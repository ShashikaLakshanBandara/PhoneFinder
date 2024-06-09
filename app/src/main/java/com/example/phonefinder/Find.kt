package com.example.phonefinder

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Find.newInstance] factory method to
 * create an instance of this fragment.
 */
class Find : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var dialog: AlertDialog

    lateinit var mAdView1: AdView
    lateinit var mAdView2: AdView

    var BS: String? = null
    var WS: String? = null
    var GS: String? = null
    var DS: String? = null
    var PS: String? = null
    var VS: String? = null
    var AS: String? = null
    var FS: String? = null
    var phoneNumber: String? = null

    lateinit var changeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val phoneNumber: EditText = view.findViewById(R.id.TFphoneNumber)
        val securityCode: EditText = view.findViewById(R.id.TFsecurityCode)
        val btncheck : Button = view.findViewById(R.id.btnCheck)




        btncheck.setOnClickListener {

            val phoneNumberText = phoneNumber.text.toString()
            val securityCodeText = securityCode.text.toString()

            val fullCode = "CommandFromPhoneFinder" +
                    "\nCommand:CHECK_VERIFICATION" +
                    "\nCode:\"$securityCodeText\""

            sendMessage(phoneNumberText, fullCode)
            showEmptyCardDialog()
        }
    }
    private fun showEmptyCardDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.empty_card_layout, null)

        dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.show()
    }
    private val smsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            BS = intent?.getStringExtra("BS")
            WS = intent?.getStringExtra("WS")
            GS = intent?.getStringExtra("GS")
            DS = intent?.getStringExtra("DS")
            PS = intent?.getStringExtra("PS")
            VS = intent?.getStringExtra("VS")
            AS = intent?.getStringExtra("AS")
            FS = intent?.getStringExtra("FS")
            phoneNumber = intent?.getStringExtra("phoneNumber")



            verifiedResponse()
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter("com.example.phonefinder.VERIFIED_RESPONSE")
        requireContext().registerReceiver(smsReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(smsReceiver)
    }

    fun verifiedResponse() {

        Log.d("shashika", "In the verifiedResponse function")
        dialog.dismiss()

        Log.d("shashika","Values in Find ")
        Log.d("shashika", "BS = $BS")
        Log.d("shashika", "WS = $WS")
        Log.d("shashika", "GS = $GS")
        Log.d("shashika", "DS = $DS")
        Log.d("shashika", "PS = $PS")
        Log.d("shashika", "VS = $VS")
        Log.d("shashika", "AS = $AS")
        Log.d("shashika", "FS = $FS")
        Log.d("shashika", "phone Number = $phoneNumber")



        val intent = Intent(activity, Results::class.java)

        intent?.putExtra("BS", BS)
        intent?.putExtra("WS", WS)
        intent?.putExtra("GS", GS)
        intent?.putExtra("DS", DS)
        intent?.putExtra("PS", PS)
        intent?.putExtra("VS", VS)
        intent?.putExtra("AS", AS)
        intent?.putExtra("FS", FS)
        intent?.putExtra("phoneNumber", phoneNumber)

        val phoneNumber = intent?.getStringExtra("phoneNumber")

        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        context?.let { MobileAds.initialize(it){} }

        val view = inflater.inflate(R.layout.fragment_find, container, false)

        mAdView1 = view.findViewById(R.id.adView1)
        val adRequest = AdRequest.Builder().build()
        mAdView1.loadAd(adRequest)

        mAdView2 = view.findViewById(R.id.adView2)
        val adRequest2 = AdRequest.Builder().build()
        mAdView2.loadAd(adRequest2)

        return view
    }

    private fun sendMessage(phoneNumber: String, message: String) {



        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.SEND_SMS),
                PERMISSION_SEND_SMS
            )
        } else {

            try {
                val smsManager: SmsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            } catch (ex: Exception) {
                Toast.makeText(context, "Failed to send message", Toast.LENGTH_SHORT).show()
                ex.printStackTrace()
            }
        }


    }

    companion object {

        private const val PERMISSION_SEND_SMS = 123

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Find().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}