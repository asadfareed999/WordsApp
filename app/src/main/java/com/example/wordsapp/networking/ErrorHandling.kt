package com.example.wordsapp.networking

import com.example.wordsapp.networking.responsemodels.ResponseError
import com.example.wordsapp.utilities.STR_UNKNOWN_ERROR
import com.google.gson.Gson
import retrofit2.Response
import java.lang.Exception

interface HttpStatusCode {
    var statusCode: Int
}

class UnknownError : Exception(STR_UNKNOWN_ERROR)

class ServerError(message: String, override var statusCode: Int) : Exception(message),
    HttpStatusCode {
}

class ClientError(message: String, override var statusCode: Int) : Exception(message),
    HttpStatusCode {

}

class DefaultErrorHandling<T>(val response: Response<T>) {

    fun handleError(): Throwable {
        return when (val statusCode = response.code()) {
            in 400..499 -> {
                ClientError(response.message(), statusCode)
            }
            in 500..599 -> {
                ServerError(response.message(), statusCode)
            }
            else -> {
                UnknownError()
            }
        }
    }
}

class CamdenCareErrorHandling<T>(val response: Response<T>) {
    private val gson = Gson()

    fun handleError(): Throwable {
        return when (val statusCode = response.code()) {
            in 400..499 -> {
                val errorBody = response.errorBody()
                val error = gson.fromJson(
                    errorBody!!.string(),
                    ResponseError::class.java
                ) as ResponseError
                ClientError(error.message, statusCode)
            }
            in 500..599 -> {
                ServerError(response.message(), statusCode)
            }
            else -> {
                UnknownError()
            }
        }
    }
}