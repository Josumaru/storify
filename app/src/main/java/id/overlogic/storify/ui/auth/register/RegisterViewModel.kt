package id.overlogic.storify.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.overlogic.storify.data.repository.AuthRepository
import id.overlogic.storify.data.source.remote.response.RegisterResponse
import id.overlogic.storify.util.Result

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _registerResult = MutableLiveData<RegisterResponse>()
    val registerResult: LiveData<RegisterResponse> = _registerResult

    private val _loading = MutableLiveData<Boolean>().apply { value = false }
    val loading: LiveData<Boolean> = _loading

    private var _error = MutableLiveData<String>().apply { value = "" }
    val error: LiveData<String> = _error

    fun register(name: String, email: String, password: String) {
        if(_registerResult.value != null) return

        authRepository.register(name, email, password).observeForever { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        _loading.value = true
                    }

                    is Result.Success -> {
                        _registerResult.value = result.data
                    }

                    is Result.Error -> {
                        _error.value = result.error
                    }
                }
            }
        }
    }
}