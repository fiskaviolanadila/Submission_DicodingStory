package com.dicoding.picodiploma.loginwithanimation.data.api

import com.dicoding.picodiploma.loginwithanimation.data.model.StoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.StoryDetailResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.SignUpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): SignUpResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): Response<StoryResponse>

    @GET("stories/{storyId}")
    suspend fun getStoryDetail(
        @Path("storyId") storyId: String,
        @Header("Authorization") token: String
    ): StoryDetailResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<StoryResponse>

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1
    ): Response<StoryResponse>

}
