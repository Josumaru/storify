package id.overlogic.storify.ui.auth.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.overlogic.storify.data.repository.AuthRepository
import id.overlogic.storify.data.source.remote.response.RegisterResponse
import id.overlogic.storify.util.Result
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application, private val authRepository: AuthRepository) :
    AndroidViewModel(application) {
    private val _result = MutableLiveData<Result<RegisterResponse>>()
    val result: LiveData<Result<RegisterResponse>> = _result

    fun register(name: String, email: String, password: String) {
        _result.value = Result.Loading
        viewModelScope.launch {
            val registerResult = authRepository.register(name, email, password)
            _result.value = registerResult
        }
    }
}
