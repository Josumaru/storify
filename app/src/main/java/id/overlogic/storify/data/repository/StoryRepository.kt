package id.overlogic.storify.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import id.overlogic.storify.data.source.local.preferences.AuthPreference
import id.overlogic.storify.data.source.remote.response.DetailResponse
import id.overlogic.storify.data.source.remote.response.RegisterResponse
import id.overlogic.storify.data.source.remote.response.StoryResponse
import id.overlogic.storify.data.source.remote.response.UploadResponse
import id.overlogic.storify.data.source.remote.retrofit.ApiService
import id.overlogic.storify.util.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository private constructor(
    private val apiService: ApiService,
) {
    private val result = MediatorLiveData<Result<StoryResponse>>()
    private val detail = MediatorLiveData<Result<DetailResponse>>()
    private val upload = MediatorLiveData<Result<UploadResponse>>()

    fun uploadStory(file: MultipartBody.Part, description: RequestBody): LiveData<Result<UploadResponse>> {
        upload.value = Result.Loading
        val client = apiService.uploadStory(file, description)
        client.enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                if (response.isSuccessful) {
                    upload.value = Result.Success(response.body()!!)
                } else {
                    upload.value = Result.Error(response.errorBody()?.string()?: "Unknown error")
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                upload.value = Result.Error(t.message?: "Unknown error")
            }
        })
        return upload
    }


    fun getDetail(id: String): LiveData<Result<DetailResponse>> {
        detail.value = Result.Loading
        val client = apiService.getDetail(id)
        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                if (response.isSuccessful) {
                    detail.value = Result.Success(response.body()!!)
                } else {
                    detail.value = Result.Error(response.errorBody()?.string() ?: "Unknown error")
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                detail.value = Result.Error(t.message?: "Unknown error")
            }
        })
        return detail
    }

    fun getStories(): LiveData<Result<StoryResponse>> {
        result.value = Result.Loading
        val client = apiService.getStories()
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                if (response.isSuccessful) {
                    result.value = Result.Success(response.body()!!)
                } else {
                    result.value = Result.Error(response.errorBody()?.string() ?: "Unknown error")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                result.value = Result.Error(t.message ?: "Unknown error")
            }
        })
        return result
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService
        ): StoryRepository {
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }.also { instance = it }
            return instance as StoryRepository
        }
    }
}