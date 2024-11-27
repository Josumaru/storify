package id.overlogic.storify.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.overlogic.storify.data.repository.StoryRepository
import id.overlogic.storify.data.source.remote.response.DetailResponse
import id.overlogic.storify.util.Result
class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>().apply { value = false }
    val loading: LiveData<Boolean> = _loading

    private val _detail = MutableLiveData<DetailResponse>()
    val detail: LiveData<DetailResponse> = _detail

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var lastFetchedId: String? = null
    fun getDetail(id: String) {
        if (lastFetchedId == id) return
        _loading.value = true
        lastFetchedId = id
        storyRepository.getDetail(id).observeForever { result ->
            when (result) {
                is Result.Loading -> _loading.value = true
                is Result.Success -> {
                    _loading.value = false
                    _detail.value = result.data
                }

                is Result.Error -> {
                    _loading.value = false
                    _error.value = result.error
                }
            }
        }
    }
}