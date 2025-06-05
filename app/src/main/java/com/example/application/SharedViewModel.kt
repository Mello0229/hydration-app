package com.example.application

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.models.*
import com.example.application.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class SharedViewModel : ViewModel() {

//    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
//    val alerts: StateFlow<List<Alert>> = _alerts

    private val _activityLogs = MutableStateFlow<List<TrainingSession>>(emptyList())
    val activityLogs: StateFlow<List<TrainingSession>> = _activityLogs

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

//    private val _athletes = MutableStateFlow<List<Athlete>>(emptyList())
//    val athletes: StateFlow<List<Athlete>> = _athletes

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

//    init {
//        fetchAlerts()
//        // fetchAthletes() will be manually called in the UI
//    }

//    private fun fetchAlerts() {
//        viewModelScope.launch {
//            try {
//                val backendAlerts = RetrofitInstance.authApi.getBackendHydrationAlert()
//                val mappedAlerts = backendAlerts.mapNotNull { backend ->
//                    val alertType = try {
//                        AlertType.valueOf(backend.alert_type.uppercase())
//                    } catch (e: IllegalArgumentException) {
//                        null
//                    }
//
//                    alertType?.let {
//                        return@mapNotNull HydrationAlert(
//                            id = UUID.randomUUID().toString(),
//                            title = when (it) {
//                                AlertType.CRITICAL -> "Critical Hydration Alert"
//                                AlertType.WARNING -> "Hydration Warning"
//                                AlertType.REMINDER -> "Daily Hydration Goal Reminder"
//                            },
//                            message = backend.description,
//                            type = it,
//                            timestamp = backend.timestamp
//                        )
//                    }
//                }
//
//                _alerts.value = mappedAlerts
//            } catch (e: Exception) {
//                Log.e("SharedViewModel", "Error fetching alerts", e)
//            }
//        }
//    }

//    fun fetchAthletes() {
//        viewModelScope.launch {
//            try {
//                val result = RetrofitInstance.authApi.getAthletes()
//                Log.d("SharedViewModel", "Fetched athletes: $result")
//                _athletes.value = result
//            } catch (e: Exception) {
//                Log.e("SharedViewModel", "Error fetching athletes", e)
//            }
//        }
//    }

    fun addActivityLog(session: TrainingSession) {
        _activityLogs.value = _activityLogs.value + session
    }

    fun hideActivityLog(session: TrainingSession) {
        _activityLogs.value = _activityLogs.value.filter { it != session }
    }
}
//    fun addHydrationAlert(hydration: Int) {
//        val alert = when {
//            hydration in 0..69 -> HydrationAlert(
//                id = UUID.randomUUID().toString(),
//                title = "Critical Hydration Alert",
//                message = "You are in a dehydrated state! Immediate hydration is recommended to prevent fatigue and performance decline.",
//                type = AlertType.CRITICAL,
//                timestamp = getCurrentDateTime()
//            )
//            hydration in 70..84 -> HydrationAlert(
//                id = UUID.randomUUID().toString(),
//                title = "Hydration Warning",
//                message = "You are slightly dehydrated. Drink 250ml of water to maintain optimal performance.",
//                type = AlertType.WARNING,
//                timestamp = getCurrentDateTime()
//            )
//            hydration >= 85 -> HydrationAlert(
//                id = UUID.randomUUID().toString(),
//                title = "Daily Hydration Goal Reminder",
//                message = "You’ve consumed 1.5L of water today. Keep going!\nYour daily hydration goal is 2.5L.",
//                type = AlertType.REMINDER,
//                timestamp = getCurrentDateTime()
//            )
//            else -> return
//        }
//        _alerts.value = _alerts.value + alert as Alert
//    }
//}