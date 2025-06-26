package com.example.application

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.models.TrainingSession
import com.example.application.storage.dataStore
import com.example.application.storage.loadLogs
import com.example.application.storage.saveLogs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

    private val _activityLogs = MutableStateFlow<List<TrainingSession>>(emptyList())
    val activityLogs: StateFlow<List<TrainingSession>> = _activityLogs

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _coachEmail = MutableStateFlow<String?>(null)
    val coachEmail: StateFlow<String?> = _coachEmail

    fun setCoachEmail(email: String) {
        _coachEmail.value = email
    }

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken

    fun setAuthToken(token: String) {
        _authToken.value = token
    }

    fun addActivityLog(session: TrainingSession) {
        _activityLogs.value = _activityLogs.value + session
    }

    fun hideActivityLog(session: TrainingSession) {
        _activityLogs.value = _activityLogs.value.filter { it != session }
    }

//    fun addActivityLogWithPersistence(session: TrainingSession, context: Context) {
//        viewModelScope.launch {
//            val prefs = context.dataStore.data.first()
//            val userEmail = prefs[stringPreferencesKey("email")] ?: return@launch
//
//            val updatedLogs = _activityLogs.value + session
//            _activityLogs.value = updatedLogs
//            saveLogs(context, userEmail, updatedLogs)
//        }
//    }

    fun addActivityLogWithPersistence(session: TrainingSession, context: Context, userEmail: String) {
        viewModelScope.launch {
            val updatedLogs = _activityLogs.value + session
            _activityLogs.value = updatedLogs
            saveLogs(context, userEmail, updatedLogs)
        }
    }

//    fun loadLogsOnAppStart(context: Context) {
//        viewModelScope.launch {
//            val prefs = context.dataStore.data.first()
//            val userEmail = prefs[stringPreferencesKey("email")] ?: return@launch
//
//            _activityLogs.value = loadLogs(context, userEmail)
//        }
//    }

    fun loadLogsOnAppStart(context: Context, userEmail: String) {
        viewModelScope.launch {
            _activityLogs.value = loadLogs(context, userEmail)
        }
    }

    fun deleteActivityLogWithPersistence(session: TrainingSession, context: Context, userEmail: String) {
        viewModelScope.launch {
            val updated = _activityLogs.value.filter { it != session }
            _activityLogs.value = updated
            saveLogs(context, userEmail, updated)
        }
    }

    fun setActivityLogs(logs: List<TrainingSession>) {
        _activityLogs.value = logs
    }
}