package com.example.wordsapp

import android.app.Application
import com.facebook.stetho.BuildConfig
import com.facebook.stetho.Stetho
import timber.log.Timber

class WordsAppApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        //init stetho
        Stetho.initializeWithDefaults(this)

        //init timber
        if (BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }
}