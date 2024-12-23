package com.dicoding.picodiploma.loginwithanimation.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.model.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.LoginResult
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginResponse = MutableStateFlow<LoginResponse?>(null)
    val loginResponse: StateFlow<LoginResponse?> get() = _loginResponse

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)

                _loginResponse.value = LoginResponse(error = false, message = "Login Berhasil", loginResult = response.loginResult)
            } catch (e: HttpException) {
                _loginResponse.value = LoginResponse(error = true, message = "Kesalahan server. Coba lagi.", loginResult = LoginResult("", "", ""))
            } catch (e: Exception) {
                _loginResponse.value = LoginResponse(error = true, message = "Terjadi kesalahan yang tidak diketahui", loginResult = LoginResult("", "", ""))
            }
        }
    }
}
