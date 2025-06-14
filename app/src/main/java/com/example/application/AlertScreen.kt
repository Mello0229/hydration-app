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
import com.example.application.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class AlertViewModel : ViewModel() {
    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts

    fun fetchAlerts() {
        viewModelScope.launch {
            try {
                val backendAlerts = RetrofitInstance.authApi.getBackendHydrationAlert()
                _alerts.value = backendAlerts.map {
                    Alert(
                        id = UUID.randomUUID().toString(),
                        alert_type = it.alert_type,
                        description = it.description,
                        status = "unresolved",
                        timestamp = it.timestamp,
                        hydration_level = it.hydration_level ?: 0f,
                        coach_message = null,
                        athlete_id = null,
                        athlete_name = null,
                        hydration_status = null,
                        status_change = null,
                        source = it.source ?: "unknown"
                    )
                }
            } catch (e: Exception) {
                Log.e("AlertViewModel", "Error fetching alerts", e)
            }
        }
    }
}

@Composable
fun AlertScreen(navController: NavController, alertViewModel: AlertViewModel = viewModel()) {
    var selectedIndex by remember { mutableStateOf(2) }

    val rawAlerts by alertViewModel.alerts.collectAsState()

    LaunchedEffect(Unit) {
        alertViewModel.fetchAlerts()
    }

    val hydrationAlerts = rawAlerts.map {
        val type = mapAlertType(it.alert_type)
        val title = when (type) {
            AlertType.DEHYDRATED -> "Critical Hydration Alert"
            AlertType.SLIGHTLY_DEHYDRATED -> "Hydration Warning"
            AlertType.HYDRATED -> "Daily Hydration Goal Reminder"
        }

        HydrationAlert(
            id = it.id,
            title = title,
            message = it.description,
            alert_type  = type,
            timestamp = it.timestamp
        )
    }

    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")

    val now = Date()

    val (newAlerts, earlierAlerts) = hydrationAlerts.partition {
        try {
            val alertTime = inputFormat.parse(it.timestamp)
            val diffMs = now.time - (alertTime?.time ?: 0L)
            val diffMin = TimeUnit.MILLISECONDS.toMinutes(diffMs)
            diffMin <= 5
        } catch (e: Exception) {
            Log.e("AlertScreen", "Error parsing timestamp: ${it.timestamp}", e)
            false
        }
    }

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

            Log.d("AlertScreen", "ALERTS COUNT = ${hydrationAlerts.size}")
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
    "critical", "dehydrated" -> AlertType.DEHYDRATED
    "warning", "slightly dehydrated" -> AlertType.SLIGHTLY_DEHYDRATED
    "reminder", "hydrated" -> AlertType.HYDRATED
    else -> AlertType.HYDRATED
}

fun formatAlertTime(timestamp: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val date = inputFormat.parse(timestamp)
        val outputFormat = SimpleDateFormat("MMMM d, yyyy | h:mm a", Locale.getDefault())
        outputFormat.format(date!!)
    } catch (e: Exception) {
        "Unknown time"
    }
}

@Composable
fun AlertCard(alert: HydrationAlert) {
    val icon = when (alert.alert_type) {
        AlertType.DEHYDRATED -> "🚨"
        AlertType.SLIGHTLY_DEHYDRATED -> "⚠️"
        AlertType.HYDRATED -> "💧"
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
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatAlertTime(alert.timestamp),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}