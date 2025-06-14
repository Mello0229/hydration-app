package com.example.application

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.application.models.Alert
import com.example.application.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale

@Composable
fun CoachAlertScreen(navController: NavHostController, viewModel: CoachAlertViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val alerts by viewModel.alerts.collectAsState()
    var selectedSort by remember { mutableStateOf("Date") }
    var selectedIndex by remember { mutableStateOf(2) }

    LaunchedEffect(Unit) {
        Log.d("CoachAlertScreen", "Calling fetchAlerts()...")
        viewModel.fetchAlerts()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(bottom = 70.dp)
        ) {
            WaveTop()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Alerts", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                SortDropdown(selectedSort) {
                    selectedSort = it
                }
            }

            Log.d("CoachAlertScreen", "Rendering ${alerts.size} alerts")
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(alerts) { alert ->
                    CoachAlertCard(alert)
                }
            }
        }

        CoachNavigationBar(
            selectedIndex = selectedIndex,
            onItemSelected = { index ->
                selectedIndex = index
                when (index) {
                    0 -> navController.navigate("coachHomeScreen")
                    1 -> navController.navigate("athletesScreen")
                    2 -> {}
                    3 -> navController.navigate("coachSettingsScreen")
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun CoachAlertCard(alert: Alert) {
    val color = when (alert.hydration_status?.lowercase(Locale.getDefault())) {
        "dehydrated" -> Color(0xFFD32F2F)
        "slightly_dehydrated" -> Color(0xFFFFA000)
        "hydrated" -> Color(0xFF00BCD4)
        else -> Color(0xFF00BCD4)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(color, shape = CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(alert.athlete_name ?: "Unknown", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(alert.coach_message ?: "No message", fontSize = 13.sp, color = Color.Black)
                }

                Text(
                    text = formatCoachTimestamp(alert.timestamp),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (alert.status?.lowercase(Locale.getDefault()) != "normal") {
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* TODO */ },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("✅ Resolve", fontSize = 12.sp, color = Color.Black)
                    }

                    OutlinedButton(
                        onClick = { /* TODO */ },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("🗑️ Dismiss", fontSize = 12.sp, color = Color.Black)
                    }
                }
            }
        }
    }
}

fun formatCoachTimestamp(isoString: String): String {
    return try {
        val inputFormats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
        )

        val date = inputFormats.firstNotNullOfOrNull { format ->
            try {
                SimpleDateFormat(format, Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.parse(isoString)
            } catch (_: Exception) {
                null
            }
        }

        if (date != null) {
            SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(date)
        } else "Unknown"

    } catch (e: Exception) {
        "Unknown"
    }
}

@Composable
fun WaveTop() {
    Image(
        painter = painterResource(id = R.drawable.wave_border),
        contentDescription = "Wave Border",
        modifier = Modifier
            .fillMaxWidth()
            .height(135.dp),
        contentScale = ContentScale.Crop,
        alignment = Alignment.BottomCenter
    )
}

@Composable
fun SortDropdown(selected: String, onSortSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.height(40.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Text(selected, color = Color.Black)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Black)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            listOf("Date", "Athlete", "Severity").forEach {
                DropdownMenuItem(
                    text = { Text(it, color = Color.Black) },
                    onClick = {
                        onSortSelected(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

class CoachAlertViewModel : ViewModel() {
    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts

    fun fetchAlerts() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.authApi.getAllCoachAlerts()
                Log.d("CoachAlertViewModel", "Fetched ${response.size} alerts")
                _alerts.value = response
            } catch (e: Exception) {
                Log.e("CoachAlertViewModel", "Error fetching alerts", e)
            }
        }
    }
}