package com.dicoding.picodiploma.loginwithanimation.view.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository

class StoryDetailViewModelFactory(private val storyRepository: StoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoryDetailViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
