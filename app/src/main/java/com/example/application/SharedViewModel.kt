package com.example.application

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.models.Athlete
import com.example.application.models.Alert
import com.example.application.models.AlertType
import com.example.application.models.HydrationAlert
import com.example.application.models.TrainingSession
import com.example.application.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

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
                val result: List<Athlete> = RetrofitInstance.authApi.getAthletes()
                _athletes.value = result
                Log.d("DEBUG_FETCH", "Received: $result")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchAlerts() {
        viewModelScope.launch {
            try {
                val result: List<Alert> = RetrofitInstance.authApi.getAlerts()
                _alerts.value = result
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

    fun addHydrationAlert(hydration: Int) {
        val alert = when {
            hydration in 0..69 -> HydrationAlert(
                id = UUID.randomUUID().toString(),
                title = "Critical Hydration Alert",
                message = "You are in a dehydrated state! Immediate hydration is recommended to prevent fatigue and performance decline.",
                type = AlertType.CRITICAL,
                timestamp = getCurrentDateTime()
            )
            hydration in 70..84 -> HydrationAlert(
                id = UUID.randomUUID().toString(),
                title = "Hydration Warning",
                message = "You are slightly dehydrated. Drink 250ml of water to maintain optimal performance.",
                type = AlertType.WARNING,
                timestamp = getCurrentDateTime()
            )
            hydration >= 85 -> HydrationAlert(
                id = UUID.randomUUID().toString(),
                title = "Daily Hydration Goal Reminder",
                message = "You’ve consumed 1.5L of water today. Keep going!\nYour daily hydration goal is 2.5L.",
                type = AlertType.REMINDER,
                timestamp = getCurrentDateTime()
            )
            else -> return
        }
        _alerts.value = _alerts.value + alert as Alert
    }
}
