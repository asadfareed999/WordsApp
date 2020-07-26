package com.example.wordsapp.prefrences
import android.app.Activity
import android.content.Context

/**
 * this is the base class for preference subclasses
 * this class contains functions to put and get key value pairs in file
 * inherit this class
 */
abstract class BasePreference(private val context: Context, private val fileName: String) {

    private val sharedPreferences = context.getSharedPreferences(fileName, Activity.MODE_PRIVATE)

    /**
     * save string value to preferences
     */
    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defValue: String): String? {
        return sharedPreferences.getString(key, defValue)
    }

    /**
     * save boolean value to preferences
     */
    fun putBool(key: String, value : Boolean){
        sharedPreferences.edit().putBoolean(key, value).apply()
    }
    fun getBool(key: String, defValue: Boolean): Boolean{
        return sharedPreferences.getBoolean(key,defValue)
    }
    fun clearSharedPrefs(){
        sharedPreferences.edit().clear().apply()
    }

}