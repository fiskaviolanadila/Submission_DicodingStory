package com.dicoding.picodiploma.loginwithanimation.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.model.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.model.StoryDetailResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.StoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.paging.StoryPagingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class StoryRepository private constructor(
    private val apiService: ApiService
) {

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(ApiConfig.getInstance()).also { instance = it }
            }
    }

    suspend fun getStories(token: String): Response<StoryResponse> {
        return withContext(Dispatchers.IO) {
            apiService.getStories(token)
        }
    }

    suspend fun getStoryDetail(storyId: String, token: String): StoryDetailResponse {
        return withContext(Dispatchers.IO) {
            apiService.getStoryDetail(storyId, token)
        }
    }

    fun getStoriesPaging(token: String, scope: CoroutineScope): LiveData<PagingData<ListStoryItem>> {
        val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 10
            ),
            pagingSourceFactory = { StoryPagingSource(apiService, formattedToken) }
        ).flow.cachedIn(scope).asLiveData()
    }
}