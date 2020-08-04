package com.example.wordsapp.networking

import com.example.wordsapp.networking.responsemodels.ResponseWords
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url


interface ApiEndpointClient {

    companion object {
        const val API_V = "v4/"
        const val SHEET_ID = "1LqVyspqf8YgCcvllT5b7B4ED2gFxRa9MN87i-Dwhg-o"
        const val API_KEY = "AIzaSyAfIcH-UbiuxC6jkj4BboVZISmNQtSO5co"
        const val GET_DATA = "${API_V}spreadsheets/${SHEET_ID}/values/Sheet1?key=${API_KEY}"

    }

    @GET(GET_DATA)
    fun getData(): Call<ResponseWords>

    @GET
    fun getUrlData(@Url url: String): Call<ResponseWords>

}