package com.dicoding.picodiploma.loginwithanimation.view.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository

class StoryViewModelFactory(private val storyRepository: StoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoryViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
