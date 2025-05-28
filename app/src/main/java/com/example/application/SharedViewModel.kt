package com.example.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

    private val _athletes = MutableStateFlow<List<Athlete>>(emptyList())
    val athletes: StateFlow<List<Athlete>> = _athletes

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts

    private val _activityLogs = MutableStateFlow<List<TrainingSession>>(emptyList())
    val activityLogs: StateFlow<List<TrainingSession>> = _activityLogs

    init {
        fetchAthletes()
        fetchAlerts()
    }

    private fun fetchAthletes() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.authApi.getAthletes()
                _athletes.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchAlerts() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.authApi.getAlerts()
                _alerts.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addActivityLog(session: TrainingSession) {
        _activityLogs.value = _activityLogs.value + session
    }
    fun hideActivityLog(session: TrainingSession) {
        _activityLogs.value = _activityLogs.value.filter { it != session }
    }
}