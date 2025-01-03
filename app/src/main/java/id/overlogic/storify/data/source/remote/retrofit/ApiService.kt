package id.overlogic.storify.data.source.remote.retrofit

import id.overlogic.storify.data.source.remote.response.DetailResponse
import id.overlogic.storify.data.source.remote.response.LoginResponse
import id.overlogic.storify.data.source.remote.response.RegisterResponse
import id.overlogic.storify.data.source.remote.response.StoryResponse
import id.overlogic.storify.data.source.remote.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @GET("stories")
    suspend fun getStories(
        @Query("location") location: Int,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Response<StoryResponse>


    @GET("stories/{id}")
    fun getDetail(
        @Path("id") id: String
    ): Call<DetailResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double? = null,
        @Part("lon") lon: Double? = null
    ): Call<UploadResponse>
}