package com.example.application.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.application.models.SignUpResponse
import com.example.application.models.UserSignup
import com.example.application.models.ErrorResponse
import com.example.application.models.TokenResponse
import com.example.application.network.RetrofitInstance
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.IOException
import retrofit2.Response

class SignupViewModel : ViewModel() {

    private val _signupResult = MutableLiveData<SignUpResponse?>()
    val signupResult: LiveData<SignUpResponse?> = _signupResult

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun signup(user: UserSignup) {
        viewModelScope.launch {
            Log.i("SIGNUP_ERROR", "Exception: "+ user)
            try {
                val response: TokenResponse = RetrofitInstance.authApi.signup(user)
                if (response != null) {
                    RetrofitInstance.authToken = response.accessToken
                    Log.d("SIGNUP_PAYLOAD", response.accessToken)
                } else {
                    _error.value = "Signup failed"
                }
            } catch (e: IOException) {
                _error.value = "Network error: ${e.message}"
            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.message}"
            }
        }
    }
}