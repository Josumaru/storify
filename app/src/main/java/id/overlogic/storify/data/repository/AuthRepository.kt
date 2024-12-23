package id.overlogic.storify.data.repository

import id.overlogic.storify.data.source.local.preferences.AuthModel
import id.overlogic.storify.data.source.local.preferences.AuthPreference
import id.overlogic.storify.data.source.remote.response.LoginResponse
import id.overlogic.storify.data.source.remote.response.RegisterResponse
import id.overlogic.storify.data.source.remote.retrofit.ApiService
import id.overlogic.storify.util.Result
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import id.overlogic.storify.util.ErrorHandler

class AuthRepository private constructor(
    private val apiService: ApiService,
    private val authPreference: AuthPreference
) {

    fun getSession(): Flow<AuthModel> {
        return authPreference.getSession()
    }

    suspend fun destroySession() {
        authPreference.destroySession()
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(email, password)
            if (response.isSuccessful) {
                response.body()?.let {
                    authPreference.saveSession(
                        AuthModel(
                            it.loginResult?.userId ?: "",
                            it.loginResult?.name ?: "",
                            it.loginResult?.token ?: "",
                            true
                        )
                    )
                    Result.Success(it)
                } ?: Result.Error("Unexpected error occurred")
            } else {
                ErrorHandler.handleErrorResponse(response)
            }
        } catch (e: HttpException) {
            val errorMessage = ErrorHandler.getErrorMessageFromException(e)
            Result.Error(errorMessage)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unexpected error occurred")
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<RegisterResponse> {
        return try {
            val response = apiService.register(name, email, password)
            if (response.isSuccessful && response.body() != null) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Unexpected error occurred")
            } else {
                ErrorHandler.handleErrorResponse(response)
            }
        } catch (e: HttpException) {
            val errorMessage = ErrorHandler.getErrorMessageFromException(e)
            Result.Error(errorMessage)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unexpected error occurred")
        }
    }


    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(
            apiService: ApiService,
            authPreference: AuthPreference
        ): AuthRepository {
            return instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService, authPreference).also { instance = it }
            }
        }
    }
}
