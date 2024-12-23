package com.dicoding.picodiploma.loginwithanimation.view.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.model.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.model.StoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Response

class StoryViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _storyList = MutableLiveData<List<ListStoryItem>>()
    val storyList: LiveData<List<ListStoryItem>> = _storyList

    private val _isLoading = MutableLiveData<Boolean>()

    private val _errorMessage = MutableLiveData<String?>()

    fun getStories(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response: Response<StoryResponse> = repository.getStories(token)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _storyList.postValue(body.listStory)
                    } else {
                        _storyList.postValue(emptyList())
                    }
                } else {
                    _errorMessage.postValue("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Exception: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getStoriesPaging(token: String): LiveData<PagingData<ListStoryItem>> {
        return repository.getStoriesPaging(token, viewModelScope)
    }
}
