package id.overlogic.storify.ui.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.overlogic.storify.R
import id.overlogic.storify.data.repository.StoryRepository
import id.overlogic.storify.data.source.remote.response.StoryResponse
import id.overlogic.storify.util.Result
import kotlinx.coroutines.launch

class MapViewModel(application: Application, private val storyRepository: StoryRepository) :
    AndroidViewModel(application) {

    private val _result = MutableLiveData<Result<StoryResponse>>()
    val result: LiveData<Result<StoryResponse>> = _result

    init {
        getStories()
    }


    private fun getStories() {
        _result.value = Result.Loading
        viewModelScope.launch {
            try {
                val storyResult = storyRepository.getStories(1)
                _result.value = storyResult
            } catch (e: Exception) {
                _result.value = Result.Error(
                    e.message
                        ?: getApplication<Application>().getString(R.string.unexpected_error_occurred)
                )
            }
        }
    }
}
