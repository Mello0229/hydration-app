package com.example.application.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.application.models.LoginResponse
import com.example.application.models.UserLogin
import com.example.application.network.RetrofitInstance
import kotlinx.coroutines.launch
import java.io.IOException

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val userSignup = UserLogin(
                    email = email,
                    password = password
                )
                val response = RetrofitInstance.authApi.login(userSignup)
                if (response.accessToken != null ) {
                    _loginResult.value = LoginResponse(
                        success = false,
                        token = response.accessToken,
                        message = "",
                        role = ""
                    )
                } else {
                    _loginResult.value = LoginResponse(
                        success = false,
                        token = null,
                        message = "",
                        role = ""
                    )
                }
            } catch (e: IOException) {
                _loginResult.value = LoginResponse(
                    success = false,
                    token = null,
                    message = "Network error: ${e.message}",
                    role = ""
                )
            } catch (e: Exception) {
                _loginResult.value = LoginResponse(
                    success = false,
                    token = null,
                    message = "Unexpected error: ${e.message}",
                    role = ""
                )
            }
        }
    }
}