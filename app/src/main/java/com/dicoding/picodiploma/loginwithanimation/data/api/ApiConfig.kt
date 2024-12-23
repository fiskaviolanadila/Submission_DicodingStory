package com.dicoding.picodiploma.loginwithanimation.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {

    companion object {
        private const val BASE_URL = "https://story-api.dicoding.dev/v1/"
        private var retrofit: Retrofit? = null

        private fun getOkHttpClient(): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            return OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request()
                    println("Request Headers: ${request.headers}")
                    chain.proceed(request)
                }
                .addInterceptor(loggingInterceptor)
                .build()
        }

        fun getInstance(): ApiService {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!.create(ApiService::class.java)
        }
    }
}
