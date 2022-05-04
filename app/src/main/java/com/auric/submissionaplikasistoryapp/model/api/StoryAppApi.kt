package com.auric.submissionaplikasistoryapp.model.api

import com.auric.submissionaplikasistoryapp.model.FileUploadResponse
import com.auric.submissionaplikasistoryapp.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface StoryAppApi {

    @POST("login")
    fun loginUser(
        @Body loginUser: LoginUser
    ): Call<UserLoginResponse>


    @POST("register")
    fun registerUser(
        @Body registerUser: RegisterUser
    ): Call<UserRegisterResponse>

    @GET("stories")
    suspend fun getAllStoriesPaging(
        @Header("Authorization") auth: String,
            @Query("page") page: Int,
            @Query("size") size: Int
    ): Response<StoriesResponse>

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") latitude: RequestBody,
        @Part("lon") longitude: RequestBody,
    ): Call<FileUploadResponse>

    @GET("stories")
    fun getAllStoriesLocation(
        @Header("Authorization") auth: String,
        @Query("location")location: Int = 1
    ): Call<StoriesResponse>
}