package com.example.application

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.application.models.AlertType
import com.example.application.models.HydrationAlert
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.application.models.Alert
import com.example.application.models.BackendHydrationAlert
import com.example.application.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AlertViewModel : ViewModel() {

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts

    private var lastAlertType: String? = null  // ✅ ADD THIS
    private var lastSentTimeMillis = 0L         // ⏱ Optional throttle logic

    init {
        fetchAlerts()
    }

    private fun fetchAlerts() {
        viewModelScope.launch {
            try {
                val backendAlerts: List<BackendHydrationAlert> = RetrofitInstance.authApi.getBackendHydrationAlert()
                _alerts.value = backendAlerts.map {
                    Alert(
                        id = UUID.randomUUID().toString(),
                        alert_type = it.alert_type,
                        description = it.description,
                        status = "unresolved",
                        timestamp = it.timestamp,
                        name = "",
                        hydration_level = extractHydrationLevel(it.description)
                    )
                }
            } catch (e: Exception) {
                Log.e("AlertViewModel", "Error fetching alerts", e)
            }
        }
    }

    // ✅ REPLACE direct calls to this function with maybeSendHydrationAlert instead
    fun sendHydrationAlertToBackend(hydrationLevel: Float) {
        val levelInt = hydrationLevel.toInt()

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.authApi.HydrationAlert(hydrationLevel = levelInt)
                if (response.isSuccessful) {
                    Log.d("HydrationAlert", "Alert sent successfully to backend.")
                    fetchAlerts()
                } else {
                    Log.e("HydrationAlert", "Backend error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HydrationAlert", "Exception sending alert", e)
            }
        }
    }

    // ✅ ADD THIS FUNCTION BELOW
    fun maybeSendHydrationAlert(hydrationLevel: Float) {
        val level = hydrationLevel.toInt()
        val currentType = when {
            level < 70 -> "CRITICAL"
            level < 85 -> "WARNING"
            else -> "REMINDER"
        }

        val now = System.currentTimeMillis()

        // Only send if the alert type changed OR 5 minutes have passed
        if (currentType != lastAlertType || now - lastSentTimeMillis > 5 * 60 * 1000) {
            lastAlertType = currentType
            lastSentTimeMillis = now
            sendHydrationAlertToBackend(hydrationLevel)
        }
    }

    private fun extractHydrationLevel(description: String): Float {
        val regex = Regex("""\d+""")
        return regex.find(description)?.value?.toFloatOrNull() ?: 0f
    }
}

@Composable
fun AlertScreen(navController: NavController, alertViewModel: AlertViewModel = viewModel()) {
    var selectedIndex by remember { mutableStateOf(2) }

    val rawAlerts by alertViewModel.alerts.collectAsState()

    val hydrationAlerts = rawAlerts.map {
        val type = mapAlertType(it.alert_type)
        val title = when (type) {
            AlertType.CRITICAL -> "Critical Hydration Alert"
            AlertType.WARNING -> "Hydration Warning"
            AlertType.REMINDER -> "Daily Hydration Goal Reminder"
        }

        HydrationAlert(
            id = it.id,
            title = title,
            message = it.description,
            alert_type  = type,
            timestamp = it.timestamp
        )
    }

    val (newAlerts, earlierAlerts) = hydrationAlerts.partition { it.timestamp == "now" }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 72.dp)) {

            Image(
                painter = painterResource(id = R.drawable.wave_border),
                contentDescription = "Wave Border",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp),
                contentScale = ContentScale.Crop,
                alignment = Alignment.BottomCenter
            )

            Text(
                text = "Alerts",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (newAlerts.isEmpty() && earlierAlerts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No activity yet",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Normal
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    if (newAlerts.isNotEmpty()) {
                        item {
                            Text(
                                text = "New",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(newAlerts) { alert ->
                            AlertCard(alert)
                        }
                    }

                    if (earlierAlerts.isNotEmpty()) {
                        item {
                            Text(
                                text = "Earlier",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        items(earlierAlerts) { alert ->
                            AlertCard(alert)
                        }
                    }
                }
            }
        }

        ButtonNavigationBar(
            selectedIndex = selectedIndex,
            onItemSelected = { index ->
                selectedIndex = index
                when (index) {
                    0 -> navController.navigate("home")
                    1 -> navController.navigate("insightsScreen")
                    2 -> {}
                    3 -> navController.navigate("settingsScreen")
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

fun mapAlertType(type: String): AlertType = when (type.lowercase()) {
    "critical" -> AlertType.CRITICAL
    "warning" -> AlertType.WARNING
    "reminder" -> AlertType.REMINDER
    else -> AlertType.REMINDER
}

@Composable
fun AlertCard(alert: HydrationAlert) {
    val icon = when (alert.alert_type) {
        AlertType.CRITICAL -> "🚨"
        AlertType.WARNING -> "⚠️"
        AlertType.REMINDER -> "💧"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "$icon ${alert.title}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = alert.message,
                fontSize = 13.sp,
                color = Color.Black
            )
        }
    }
}