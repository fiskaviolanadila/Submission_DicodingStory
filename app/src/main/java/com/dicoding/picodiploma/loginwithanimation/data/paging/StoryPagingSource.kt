package com.dicoding.picodiploma.loginwithanimation.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.model.ListStoryItem

class StoryPagingSource(
    private val apiService: ApiService,
    private val token: String
) : PagingSource<Int, ListStoryItem>() {

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        val position = params.key ?: INITIAL_PAGE_INDEX
        println("PagingSource Load - Current Page: $position, LoadSize: ${params.loadSize}")

        return try {
            val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            val response = apiService.getStories(formattedToken, position, params.loadSize)

            if (response.isSuccessful) {
                val stories = response.body()?.listStory ?: emptyList()
                val nextKey = if (stories.isEmpty()) null else position + 1

                println("Page Loaded Successfully - Current Page: $position, Stories Count: ${stories.size}, NextKey: $nextKey")
                LoadResult.Page(
                    data = stories,
                    prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                    nextKey = nextKey
                )
            } else {
                println("API Error: ${response.code()} - ${response.message()}")
                LoadResult.Error(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            println("Exception in PagingSource Load: ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val closestPage = state.closestPageToPosition(anchorPosition)
        val refreshKey = closestPage?.prevKey?.plus(1) ?: closestPage?.nextKey?.minus(1)
        println("Refresh Key: $refreshKey")
        return refreshKey
    }
}
