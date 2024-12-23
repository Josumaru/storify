package id.overlogic.storify.util

import com.google.gson.Gson
import id.overlogic.storify.data.source.remote.response.ErrorResponse
import retrofit2.HttpException

class ErrorHandler {
    companion object {
        fun handleErrorResponse(response: retrofit2.Response<*>): Result.Error {
            val errorMessage = response.errorBody()?.string()?.let {
                Gson().fromJson(it, ErrorResponse::class.java).message
            } ?: "Unknown error occurred"

            return Result.Error(errorMessage)
        }

        fun getErrorMessageFromException(e: HttpException): String {
            val errorBody = e.response()?.errorBody()?.string()
            return if (!errorBody.isNullOrEmpty()) {
                try {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    errorResponse.message ?: "Unknown error occurred"
                } catch (exception: Exception) {
                    "An error occurred while parsing the error message"
                }
            } else {
                "An unknown error occurred"
            }
        }
    }
}