package com.dicoding.picodiploma.loginwithanimation.data

import com.dicoding.picodiploma.loginwithanimation.data.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.model.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.SignUpResponse
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    suspend fun register(name: String, email: String, password: String): SignUpResponse {
        return withContext(Dispatchers.IO) {
            apiService.register(name, email, password)
        }
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return withContext(Dispatchers.IO) {
            apiService.login(email, password)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(userPreference: UserPreference, apiService: ApiService): UserRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserRepository(userPreference, apiService).also { INSTANCE = it }
            }
        }
    }
}
