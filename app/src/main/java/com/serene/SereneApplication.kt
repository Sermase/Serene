package com.serene

import android.app.Application
import com.google.firebase.FirebaseApp

class SereneApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase initialization placeholder. The Firebase SDK will attempt to
        // load the google-services.json configuration if it is present.
        FirebaseApp.initializeApp(this)
    }
}
