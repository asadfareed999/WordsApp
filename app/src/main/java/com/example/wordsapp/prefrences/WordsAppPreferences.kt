package com.example.wordsapp.prefrences

import android.content.Context
import com.example.wordsapp.utilities.STR_PREF_NAME

class WordsAppPreferences(private val context: Context) :
    BasePreference(context, STR_PREF_NAME) {

    companion object {
        const val DEF_VALUE_STRING = ""
        const val PREF_KEY_NAME = "name_key"
    }

    fun saveName(name: String) {
        putString(PREF_KEY_NAME, name)
    }

    fun getName(): String {
        return getString(PREF_KEY_NAME, DEF_VALUE_STRING)!!
    }

    fun clearPrefs() {
        clearSharedPrefs()
    }
}