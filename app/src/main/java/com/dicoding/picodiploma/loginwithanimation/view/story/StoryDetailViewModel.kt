package com.dicoding.picodiploma.loginwithanimation.view.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.model.Story
import kotlinx.coroutines.launch

class StoryDetailViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _story = MutableLiveData<Story?>()
    val story: LiveData<Story?> = _story

    fun getStoryDetail(storyId: String, token: String) {
        viewModelScope.launch {
            val response = repository.getStoryDetail(storyId, token)
            if (response.error == false) {
                _story.postValue(response.story)
            }
        }
    }
}
