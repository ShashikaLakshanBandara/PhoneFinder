package com.example.phonefinder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }




    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val settingsLayout: LinearLayout = view.findViewById(R.id.allSettings)
        val userName = view.findViewById<TextView>(R.id.tvName)
        val emailAddress = view.findViewById<TextView>(R.id.tvEmailAddress)
        val profilePicture = view.findViewById<ImageView>(R.id.circleImageView2)


        val db = Room.databaseBuilder(
            requireContext().applicationContext,
            SettingsDatabase::class.java,"settings"
        ).build()

        val viewModelScope = lifecycleScope

        viewModelScope.launch {
            val settings = withContext(Dispatchers.IO) {
                db.SettingsDAO().getSettingsById(1)
            }
            settings?.let {
                userName.setText(it.userName)
                emailAddress.setText(it.emailAddress)
                var resourceName = it.propicPath
                var resourceId =resources.getIdentifier(resourceName,"drawable",requireContext().packageName)
                profilePicture.setImageResource(resourceId)

            } ?: run {
                userName.setText("User Name")
                emailAddress.setText("Example@gmail.com")
            }
        }



        settingsLayout.setOnClickListener {
            val intent = Intent(activity, Settings::class.java)
            startActivity(intent)

        }

        val contactUsLayout: LinearLayout = view.findViewById(R.id.contactUs)
        contactUsLayout.setOnClickListener {
            val intent = Intent(activity, ContactUs::class.java)
            startActivity(intent)
        }

        val tutorial: LinearLayout = view.findViewById(R.id.tutorials)
        tutorial.setOnClickListener {
            val intent = Intent(activity, Tutorials::class.java)
            startActivity(intent)
        }

        val about: LinearLayout = view.findViewById(R.id.aboutApp)
        about.setOnClickListener {
            val intent = Intent(activity, about::class.java)
            startActivity(intent)
        }








        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}