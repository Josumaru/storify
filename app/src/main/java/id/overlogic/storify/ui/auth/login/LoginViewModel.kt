package id.overlogic.storify.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import id.overlogic.storify.data.repository.AuthRepository
import id.overlogic.storify.data.source.remote.response.ErrorResponse
import id.overlogic.storify.data.source.remote.response.LoginResponse
import id.overlogic.storify.util.Result
import retrofit2.HttpException

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<LoginResponse>()
    val loginResult: LiveData<LoginResponse> = _loginResult
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun login(email: String, password: String) {
        if (_loginResult.value != null) return
        _loading.value = true
        try {
            authRepository.login(email, password).observeForever { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            _loading.value = true
                        }

                        is Result.Success -> {
                            _loginResult.value = result.data
                            _loading.value = false
                        }

                        is Result.Error -> {
                            _error.value = result.error
                            _loading.value = false
                        }
                    }
                }
            }

        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            _error.value = errorBody.message.toString()
            _loading.value = true
        }

    }
}