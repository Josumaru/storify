package id.overlogic.storify.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import id.overlogic.storify.data.source.local.preferences.AuthModel
import id.overlogic.storify.data.source.local.preferences.AuthPreference
import id.overlogic.storify.data.source.remote.response.LoginResponse
import id.overlogic.storify.data.source.remote.response.RegisterResponse
import id.overlogic.storify.data.source.remote.retrofit.ApiConfig
import id.overlogic.storify.data.source.remote.retrofit.ApiService
import id.overlogic.storify.util.Result
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class AuthRepository private constructor(
    private val apiService: ApiService,
    private val authPreference: AuthPreference,
) {
    private val result = MediatorLiveData<Result<RegisterResponse>>()
    private val loginResult = MediatorLiveData<Result<LoginResponse>>()

    suspend fun saveSession(user: AuthModel) {
        authPreference.saveSession(user)
    }

    fun getSession(): Flow<AuthModel> {
        return authPreference.getSession()
    }

    suspend fun destroySession() {
        authPreference.destroySession()
    }


    fun login(email: String, password: String): LiveData<Result<LoginResponse>> {
        loginResult.value = Result.Loading
        val client = apiService.login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                val responseBody = response.body()
                if (responseBody != null) {
                    loginResult.value = Result.Success(responseBody)
                    CoroutineScope(Dispatchers.IO).launch {
                        authPreference.saveSession(
                            AuthModel(
                                responseBody.loginResult?.userId.toString(),
                                responseBody.loginResult?.name.toString(),
                                responseBody.loginResult?.token.toString()
                            )
                        )
                        ApiConfig.setToken(responseBody.loginResult?.token.toString())
                    }
                } else {
                    loginResult.value = Result.Error("Something went wrong")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginResult.value = Result.Error("${t.message}")
            }
        })
        return loginResult
    }

    fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> {
        result.value = Result.Loading
        val client = apiService.register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                val responseBody = response.body()
                if (responseBody != null) {
                    result.value = Result.Success(responseBody)
                } else {
                    result.value = Result.Error("Something went wrong")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                result.value = Result.Error("${t.message}")
            }
        })
        return result
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            apiService: ApiService,
            authPreference: AuthPreference,
        ): AuthRepository {
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService, authPreference)
            }.also { instance = it }
            return instance as AuthRepository
        }
    }
}