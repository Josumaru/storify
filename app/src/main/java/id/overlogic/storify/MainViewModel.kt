package id.overlogic.storify

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import id.overlogic.storify.data.repository.AuthRepository
import id.overlogic.storify.data.source.local.preferences.AuthModel
import kotlinx.coroutines.launch


class MainViewModel(private val repository: AuthRepository) : ViewModel() {
    fun getSession(): LiveData<AuthModel> {
        return repository.getSession().asLiveData()
    }

    fun destroySession() {
        viewModelScope.launch {
            repository.destroySession()
        }
    }
}