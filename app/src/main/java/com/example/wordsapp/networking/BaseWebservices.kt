package com.example.wordsapp.networking

import com.facebook.stetho.okhttp3.BuildConfig
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface OnResponseListener<T> {
    fun onSuccess(response: T?)
    fun onFailure(t: Throwable)
    fun onCancel()
}

object BaseWebservices {

    private const val BASE_URL = "https://sheets.googleapis.com/"

    private var retrofit: Retrofit? = null
    private val okHttpClientBuilder = OkHttpClient.Builder()
    private lateinit var okHttpClient: OkHttpClient
    private val loggingInterceptor = getLoggingInterceptor()
    private val stethoInterceptor = getStethoInterceptor()

    private fun createHttpClient() {
        if (BuildConfig.DEBUG) {
            if (!okHttpClientBuilder.interceptors().contains(loggingInterceptor))
                okHttpClientBuilder.addInterceptor(loggingInterceptor)
            if (!okHttpClientBuilder.interceptors().contains(stethoInterceptor))
                okHttpClientBuilder.addNetworkInterceptor(stethoInterceptor)
        }
        okHttpClient = okHttpClientBuilder.build()
    }

    /** Interceptors **/
    private fun getLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.BASIC
        }
        return httpLoggingInterceptor
    }

    private fun getStethoInterceptor(): StethoInterceptor {
        return StethoInterceptor()
    }

    /** Interceptors end**/

    private fun init() {
        if (retrofit == null) {
            createHttpClient()
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient).addConverterFactory(
                    GsonConverterFactory.create()
                )
                .build()
        }
    }

    fun getApiEndpointClient(): ApiEndpointClient {
        init()
        return retrofit!!.create(ApiEndpointClient::class.java)
    }

    fun <T> executeApi(call: Call<T>, listener: OnResponseListener<T>) {
        val callback = object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                if (call.isCanceled) {
                    listener.onCancel()
                } else {
                    listener.onFailure(t)
                }
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                when {
                    response.isSuccessful -> listener.onSuccess(response.body())
                    call.isCanceled -> listener.onCancel()
                    else -> {
                        val error = DefaultErrorHandling(response).handleError()
                        listener.onFailure(error)
                    }
                }
            }

        }
        call.enqueue(callback)
    }
}