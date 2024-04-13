package com.example.handyapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.handyapp.ui.theme.HandyAppTheme
import com.google.android.material.bottomappbar.BottomAppBar
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomnavigation.BottomNavigationMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.Clock


class MainActivity : FragmentActivity() {
    private lateinit var bottomBar: BottomNavigationView
    private lateinit var redialButton: ImageButton
//    Create an app called Handy App. The app should have the following functions:
//    - a stop watch with the ability to start and reset. done
//    - a version of MapLocation
//    - a alarm (should redirect to alarms) done
//    - a contacts  sms/call  function done
//    - a calendar capability to schedule events (send to google calendar) done
//    -  the ability to redial the last call made on the device done

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_layout)
        redialButton = findViewById(R.id.redialButton)
        bottomBar = findViewById(R.id.bottom_navigation)
        navFunction(R.id.contacts)
        bottomBar.setOnNavigationItemSelectedListener {navFunction(it.itemId) }
        redialButton.setOnClickListener{redialNum()}
    }


    private fun navFunction(itemId: Int): Boolean{
        when (itemId){
            R.id.contacts ->{
                loadFragment(ContactFragment())
                return true
            }
            R.id.stopwatch ->{
                loadFragment(StopwatchFragment())
                return true
            }
            R.id.calendar ->{
                val calendarIntent = Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                startActivity(calendarIntent)

            }
            R.id.alarm ->{
                val intent = Intent(AlarmClock.ACTION_SET_ALARM)
                // Verify if there's an app to handle this intent
                startActivity(intent)
            }

        }
//inflater.inflate(R.layout.stopwatch_fragment, container,false)
        //Try show and hide if we are losing state or cannot switch


        return true
    }


    private fun redialNum(){
        val phoneNumber = getSharedPreferences("contactPreferences", Context.MODE_PRIVATE).getString("lastNumber", "")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
            val dialIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            startActivity(dialIntent)
        }
    }
    private fun loadFragment(frag: Fragment){
        val fragmentManager = supportFragmentManager

        if (null == fragmentManager.findFragmentById(frag.id)){
            val fragTransaction = fragmentManager.beginTransaction()

            if (fragmentManager.findFragmentById(R.id.frameFragment) == null ){
                fragTransaction.add(R.id.frameFragment, frag)
            }else{
                fragTransaction.replace(R.id.frameFragment, frag)
                fragTransaction.addToBackStack(null)
            }

            fragTransaction.commit()
        }


    }
}

