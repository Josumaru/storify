package id.overlogic.storify.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import id.overlogic.storify.data.source.local.database.StoryDatabase
import id.overlogic.storify.data.source.local.entity.StoryEntity
import id.overlogic.storify.data.source.remote.mediator.StoryRemoteMediator
import id.overlogic.storify.data.source.remote.response.DetailResponse
import id.overlogic.storify.data.source.remote.response.StoryResponse
import id.overlogic.storify.data.source.remote.response.UploadResponse
import id.overlogic.storify.data.source.remote.retrofit.ApiService
import id.overlogic.storify.util.ErrorHandler
import id.overlogic.storify.util.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase,
) {
    private val detail = MediatorLiveData<Result<DetailResponse>>()
    private val upload = MediatorLiveData<Result<UploadResponse>>()

    fun uploadStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double? = null,
        lon: Double? = null
    ): LiveData<Result<UploadResponse>> {
        upload.value = Result.Loading
        val client = apiService.uploadStory(file, description, lat, lon)
        client.enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                if (response.isSuccessful) {
                    upload.value = Result.Success(response.body()!!)
                } else {
                    upload.value = Result.Error(response.errorBody()?.string() ?: "Unknown error")
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                upload.value = Result.Error(t.message ?: "Unknown error")
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
                detail.value = Result.Error(t.message ?: "Unknown error")
            }
        })
        return detail
    }

    fun getPageStory(): LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            },
        ).liveData
    }

    suspend fun getStories(location: Int): Result<StoryResponse> {
        return try {
            val response = apiService.getStories(location)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Unexpected error occurred")
            } else {
                ErrorHandler.handleErrorResponse(response)

            }
        } catch (e: HttpException) {
            val errorMessage = ErrorHandler.getErrorMessageFromException(e)
            Result.Error(errorMessage)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unexpected error occurred")
        }

    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            storyDatabase: StoryDatabase
        ): StoryRepository {
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, storyDatabase)
            }.also { instance = it }
            return instance as StoryRepository
        }
    }
}