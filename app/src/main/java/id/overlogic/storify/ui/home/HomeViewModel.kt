package id.overlogic.storify.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.overlogic.storify.data.repository.AuthRepository
import id.overlogic.storify.data.repository.StoryRepository
import id.overlogic.storify.data.source.local.preferences.AuthModel
import id.overlogic.storify.data.source.remote.response.ListStoryItem
import id.overlogic.storify.data.source.remote.response.StoryResponse
import id.overlogic.storify.util.Result

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>().apply { value = false }
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _stories = MutableLiveData<List<ListStoryItem>>().apply { value = emptyList() }
    val stories: LiveData<List<ListStoryItem>> = _stories

    init {
        getStories()
    }

    fun refreshStories() {
        _stories.value = emptyList()
        getStories()
    }

    private fun getStories() {
        if (_stories.value?.isNotEmpty() == true) {
            return
        }

        storyRepository.getStories().observeForever { result ->
            when (result) {
                is Result.Loading -> {
                    _loading.value = true
                }

                is Result.Success -> {
                    _stories.value = result.data.listStory
                    _loading.value = false
                }

                is Result.Error -> {
                    _error.value = result.error
                    _loading.value = false
                }
            }
        }

    }
}