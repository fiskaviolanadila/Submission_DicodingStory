package com.dicoding.picodiploma.loginwithanimation.view.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.model.SignUpResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _signUpResponse = MutableStateFlow<SignUpResponse?>(null)
    val signUpResponse: StateFlow<SignUpResponse?> get() = _signUpResponse

    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.register(name, email, password)
                _signUpResponse.value = response
            } catch (e: Exception) {
                _signUpResponse.value = SignUpResponse(true, "Sign Up Failed: ${e.message}")
            }
        }
    }
}
