package com.example.application

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.models.UserProfile
import com.example.application.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileSetupViewModel : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _birthday = MutableStateFlow("")
    val birthday: StateFlow<String> = _birthday

    private val _weight = MutableStateFlow("")
    val weight: StateFlow<String> = _weight

    private val _gender = MutableStateFlow("")
    val gender: StateFlow<String> = _gender

    private val _sports = MutableStateFlow("")
    val sports: StateFlow<String> = _sports

    private val _coachAssigned = MutableStateFlow("")
    val coachAssigned: StateFlow<String> = _coachAssigned

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun onFullNameChange(value: String) { _name.value = value }
    fun onBirthdayChange(value: String) { _birthday.value = value }
    fun onWeightChange(value: String) { _weight.value = value }
    fun onGenderChange(value: String) { _gender.value = value }
    fun onSportsChange(value: String) { _sports.value = value }
    fun onCoachAssignedChange(value: String) { _coachAssigned.value = value }

    fun submitProfile(email: String, navToHome: () -> Unit) {
        if (_name.value.isBlank() ||
            _birthday.value.isBlank() ||
            _weight.value.isBlank() ||
            _gender.value.isBlank() ||
            _sports.value.isBlank() ||
            _coachAssigned.value.isBlank()
        ) {
            _errorMessage.value = "Please fill in all fields before proceeding."
            return
        }

        viewModelScope.launch {
            try {
                val weight = _weight.value.toFloatOrNull()
                if (weight == null) {
                    _errorMessage.value = "Invalid weight input"
                    return@launch
                }

                val profile = UserProfile(
                    name = _name.value,
                    dob = _birthday.value,
                    weight = weight,
                    gender = _gender.value.lowercase(),
                    sport = _sports.value,
                    coach_name = _coachAssigned.value
                )

                Log.d("PROFILE_PAYLOAD", "$profile")
                val updateResponse = RetrofitInstance.authApi.updateProfile(profile)
                Log.d("UPDATE_RESPONSE", "$updateResponse")
                navToHome()
            } catch (e: Exception) {
                Log.e("ERROR_RESPONSE", "Exception during profile submit", e)
                _errorMessage.value = "Failed to save profile: ${e.localizedMessage}"
            }
        }
    }
//                Log.d("PROFILE_PAYLOAD", "$profile")
//                val response = RetrofitInstance.authApi.updateProfile(profile)
//                Log.d("PROFILE_RESPONSE", "$response")
//                navToHome()
//            } catch (e: Exception) {
//                Log.e("ERROR_RESPONSE", "Exception during profile save", e)
//                _errorMessage.value = "Failed to save profile: ${e.localizedMessage}"
//            }
//        }
//    }
}