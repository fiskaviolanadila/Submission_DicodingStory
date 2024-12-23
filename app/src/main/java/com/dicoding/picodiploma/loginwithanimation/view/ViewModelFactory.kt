package com.dicoding.picodiploma.loginwithanimation.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.view.login.LoginViewModel
import com.dicoding.picodiploma.loginwithanimation.view.main.MainViewModel
import com.dicoding.picodiploma.loginwithanimation.view.signup.SignUpViewModel
import com.dicoding.picodiploma.loginwithanimation.view.story.StoryViewModel

class ViewModelFactory(
    private val userRepository: UserRepository? = null,
    private val storyRepository: StoryRepository? = null
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                if (userRepository != null) {
                    MainViewModel(userRepository) as T
                } else {
                    throw IllegalArgumentException("UserRepository is required for MainViewModel")
                }
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                if (userRepository != null) {
                    LoginViewModel(userRepository) as T
                } else {
                    throw IllegalArgumentException("UserRepository is required for LoginViewModel")
                }
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                if (userRepository != null) {
                    SignUpViewModel(userRepository) as T
                } else {
                    throw IllegalArgumentException("UserRepository is required for SignUpViewModel")
                }
            }
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                if (storyRepository != null) {
                    StoryViewModel(storyRepository) as T
                } else {
                    throw IllegalArgumentException("StoryRepository is required for StoryViewModel")
                }
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    userRepository = Injection.provideRepository(context),
                    storyRepository = Injection.provideStoryRepository()
                ).also { instance = it }
            }
    }
}
