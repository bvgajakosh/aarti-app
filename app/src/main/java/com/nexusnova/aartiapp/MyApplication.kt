package com.nexusnova.aartiapp

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the default FirebaseApp instance
        FirebaseApp.initializeApp(this)
    }
}
