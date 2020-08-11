package com.example.wordsapp.prefrences

import android.content.Context
import com.example.wordsapp.utilities.STR_PREF_NAME

class WordsAppPreferences(private val context: Context) :
    BasePreference(context, STR_PREF_NAME) {

    companion object {
        const val DEF_VALUE_STRING = ""
        const val PREF_KEY_WORD = "word_key"
        const val PREF_KEY_MEANING = "meaning_key"
        const val PREF_KEY_SORTING = "sorting_key"
        const val PREF_KEY_URL = "url_key"
        const val PREF_KEY_ShEET_NO = "sheet_key"


    }

    fun saveWord(name: String) {
        putString(PREF_KEY_WORD, name)
    }

    fun getWord(): String {
        return getString(PREF_KEY_WORD, DEF_VALUE_STRING)!!
    }

    fun saveMeaning(name: String) {
        putString(PREF_KEY_MEANING, name)
    }

    fun getMeaning(): String {
        return getString(PREF_KEY_MEANING, DEF_VALUE_STRING)!!
    }

    fun setSortingType(name: String) {
        putString(PREF_KEY_SORTING, name)
    }

    fun getSortingType(): String {
        return getString(PREF_KEY_SORTING, DEF_VALUE_STRING)!!
    }

    var url: String
        get() = getString(PREF_KEY_URL, "")!!
        set(value) = putString(PREF_KEY_URL, value)

    var sheetNO: String
        get() = getString(PREF_KEY_ShEET_NO, "")!!
        set(value) = putString(PREF_KEY_ShEET_NO, value)


    fun clearPrefs() {
        clearSharedPrefs()
    }
}