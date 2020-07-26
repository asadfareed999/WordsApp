package com.example.wordsapp.networking.responsemodels

import com.google.gson.annotations.SerializedName


data class ResponseWords(
    val range : String,
    val majorDimension : String,
    val values : List<List<String>>
)