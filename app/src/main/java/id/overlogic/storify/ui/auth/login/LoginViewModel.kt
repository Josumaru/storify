package id.overlogic.storify.ui.auth.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.overlogic.storify.R
import id.overlogic.storify.data.repository.AuthRepository
import id.overlogic.storify.data.source.remote.response.LoginResponse
import id.overlogic.storify.util.Result
import kotlinx.coroutines.launch

class LoginViewModel(application: Application, private val authRepository: AuthRepository) :
    AndroidViewModel(application) {

    private val _result = MutableLiveData<Result<LoginResponse>>()
    val result: LiveData<Result<LoginResponse>> = _result

    fun login(email: String, password: String) {
        _result.value = Result.Loading
        viewModelScope.launch {
            try {
                val loginResult = authRepository.login(email, password)
                _result.value = loginResult
            } catch (e: Exception) {
                _result.value = Result.Error(
                    e.message
                        ?: getApplication<Application>().getString(R.string.unexpected_error_occurred)
                )
            }
        }
    }
}
