package com.sliebald.cula

import android.app.Application

/**
 * Application class to provide access to the application context for e.g. the Database.
 * Based on https://stackoverflow.com/questions/21818905/get-application-context-from-non
 * -activity-singleton-class
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        /**
         * Get the Application context of the App.
         *
         * @return The [android.content.Context] of this app.
         */
        lateinit var context: MyApplication
            private set
    }
}