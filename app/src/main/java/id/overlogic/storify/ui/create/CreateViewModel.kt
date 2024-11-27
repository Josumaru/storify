package id.overlogic.storify.ui.create

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.overlogic.storify.data.repository.StoryRepository
import id.overlogic.storify.data.source.remote.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import id.overlogic.storify.util.Result

class CreateViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _uri = MutableLiveData<Uri>()
    val uri: LiveData<Uri> = _uri

    private val _upload = MutableLiveData<UploadResponse>()
    val upload: LiveData<UploadResponse> = _upload


    fun setUri(uri: Uri) {
        _uri.value = uri
    }

    fun uploadStory(file: MultipartBody.Part, description: RequestBody) {
        _isLoading.value = true
        storyRepository.uploadStory(file, description).observeForever { result ->
            if(result != null){
                when(result){
                    is Result.Loading -> {
                        _isLoading.value = true
                    }
                    is Result.Success -> {
                        _upload.value = result.data
                        _isLoading.value = false
                    }
                    is Result.Error -> {
                        _isLoading.value = false
                        _error.value = result.error
                    }
                }
            }
        }
    }
}