package com.nexusnova.aartiapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.nexusnova.aartiapp.R
import com.nexusnova.aartiapp.data.remote.FirestoreService

class MainActivity : AppCompatActivity() {

    // Duration of the splash screen in milliseconds
    private val SPLASH_DELAY = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1) Initialize Firestore offline persistence
        FirestoreService.db

        // 2) Show your splash layout
        setContentView(R.layout.activity_main)

        // 3) After the delay, start CategoryListActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, CategoryListActivity::class.java))
            finish()  // so user can't return here with back button
        }, SPLASH_DELAY)
    }
}
