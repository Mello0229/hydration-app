package com.example.application

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.application.models.HealthStats
import com.example.application.models.SensorData
import com.example.application.network.RetrofitInstance
import com.example.application.models.TrainingSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

fun getCurrentDateTime(): String {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
    return dateFormat.format(Date())
}
@Composable
fun HomeScreen(navController: NavHostController, sharedViewModel: SharedViewModel) {
    var isActivityStarted by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0L) }
    var activityTitle by remember { mutableStateOf("") }
    var activityType by remember { mutableStateOf("") }
    var activityDescription by remember { mutableStateOf("") }
    val hydrationStart by remember { mutableStateOf(0) }
    val hydrationEnd by remember { mutableStateOf(0) }
    val heartRateStart by remember { mutableStateOf(0) }
    val heartRateEnd by remember { mutableStateOf(0) }
    val temperatureStart by remember { mutableStateOf(0.0) }
    val temperatureEnd by remember { mutableStateOf(0.0) }
    val skinConductanceStart by remember { mutableStateOf(0.0) }
    val skinConductanceEnd by remember { mutableStateOf(0.0) }
    var showActivityForm by remember { mutableStateOf(false) }
    var showEndConfirmation by remember { mutableStateOf(false) }
    var showCancelConfirmation by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
//    var isWifiOn by remember { mutableStateOf(false) }
    var healthStats by remember { mutableStateOf(HealthStats()) }
    var selectedIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current
    var capturedStartStats by remember { mutableStateOf<HealthStats?>(null) }
    var capturedEndStats by remember { mutableStateOf<HealthStats?>(null) }

    LaunchedEffect(Unit) {
        try {
            healthStats = RetrofitInstance.authApi.getAthleteVitals()
            Log.d("AthletesScreen", "Fetched athlete vitals: $healthStats")
        } catch (e: Exception) {
            Log.e("AthletesScreen", "Error fetching athletes: ${e.message}")
        }
    }

    LaunchedEffect(Unit) {
        sharedViewModel.loadLogsOnAppStart(context)
    }

    LaunchedEffect(isActivityStarted) {
        while (isActivityStarted) {
            delay(1000)
            elapsedTime += 1
        }
    }

    LaunchedEffect(true) {
        while (true) {
            try {
                healthStats = RetrofitInstance.authApi.getAthleteVitals()
                Log.d("HomeScreen", "Auto-refreshed vitals: $healthStats")
            } catch (e: Exception) {
                Log.e("HomeScreen", "Auto-refresh error: ${e.message}")
            }
            delay(3_000)
        }
    }

    if (showActivityForm) {
        AlertDialog(
            onDismissRequest = { showActivityForm = false },
            confirmButton = {
                Button(
                    onClick = {
                        // Capture sensor end values
                        val startStats = capturedStartStats ?: healthStats
                        val endStats = capturedEndStats ?: healthStats
                        val hydrationEnd = (endStats.hydration_percent.toIntOrNull() ?: 0).toString()
                        val heartRateEnd = healthStats.heart_rate.toDoubleOrNull() ?: 0.0
                        val temperatureEnd = healthStats.body_temperature.toDoubleOrNull() ?: 0.0
                        val skinConductanceEnd = healthStats.skin_conductance.toDoubleOrNull() ?: 0.0
                        val ecgEnd = healthStats.ecg_sigmoid.toDoubleOrNull() ?: 0.0

                        // Build session with final duration
                        val session = TrainingSession(
                            title = activityTitle,
                            date = getCurrentDateTime().split(" - ")[0],
                            time = getCurrentDateTime().split(" - ")[1],
                            duration = "${elapsedTime / 60}m ${elapsedTime % 60}s",
                            hydrationStart = (startStats.hydration_percent.toIntOrNull() ?: 0).toString(),
                            hydrationEnd = (endStats.hydration_percent.toIntOrNull() ?: 0).toString(),
                            hydrationPercentStart = startStats.hydration_percent.toIntOrNull() ?: 0,
                            hydrationPercentEnd = endStats.hydration_percent.toIntOrNull() ?: 0,
                            heartRateStart = startStats.heart_rate.toDoubleOrNull() ?: 0.0,
                            heartRateEnd = endStats.heart_rate.toDoubleOrNull() ?: 0.0,
                            temperatureStart = startStats.body_temperature.toDoubleOrNull() ?: 0.0,
                            temperatureEnd = endStats.body_temperature.toDoubleOrNull() ?: 0.0,
                            skinConductanceStart = startStats.skin_conductance.toDoubleOrNull() ?: 0.0,
                            skinConductanceEnd = endStats.skin_conductance.toDoubleOrNull() ?: 0.0,
                            ecgStart = startStats.ecg_sigmoid.toDoubleOrNull() ?: 0.0,
                            ecgEnd = endStats.ecg_sigmoid.toDoubleOrNull() ?: 0.0,
                            description = activityDescription,
                            activity_type = activityType,
                            sessionTitle = activityTitle
                        )

//                        sharedViewModel.addActivityLog(session)
                        sharedViewModel.addActivityLogWithPersistence(session, context)

                        // ✅ Reset state only AFTER saving session
                        showActivityForm = false
                        isActivityStarted = false
                        elapsedTime = 0L
                        activityTitle = ""
                        activityType = ""
                        activityDescription = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0496E5))
                ) {
                    Text("Finish", color = Color.Black)
                }
            },
            title = { Text("Activity Summary") },
            text = {
                Column {
                    OutlinedTextField(
                        value = activityTitle,
                        onValueChange = { activityTitle = it },
                        label = { Text("Title of Activity") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = activityType,
                        onValueChange = { activityType = it },
                        label = { Text("Type of Activity") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = activityDescription,
                        onValueChange = { activityDescription = it },
                        label = { Text("Description / Feeling") },
                        maxLines = 4
                    )
                }
            },
            containerColor = Color.White
        )
    }

    if (showEndConfirmation) {
        AlertDialog(
            onDismissRequest = { showEndConfirmation = false },
            confirmButton = {
                Button(
                    onClick = {
                        sharedViewModel.viewModelScope.launch {
                            try {
                                val latest = RetrofitInstance.authApi.getAthleteVitals()
                                capturedEndStats = latest
                                endTime = getCurrentDateTime()
                                isActivityStarted = false
                                showActivityForm = true
                                showEndConfirmation = false
                            } catch (e: Exception) {
                                Log.e("EndCapture", "Failed to fetch vitals at end: ${e.message}")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0496E5))
                ) {
                    Text("Confirm", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showEndConfirmation = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("Cancel", color = Color.White)
                }
            },
            title = { Text("Confirm End Activity") },
            text = { Text("Are you sure you want to end this activity?") },
            containerColor = Color.White
        )
    }

    if (showCancelConfirmation) {
        AlertDialog(
            onDismissRequest = { showCancelConfirmation = false },
            confirmButton = {
                Button(
                    onClick = {
                        isActivityStarted = false
                        elapsedTime = 0L
                        startTime = ""
                        endTime = ""
                        showCancelConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0496E5))
                ) {
                    Text("Confirm", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showCancelConfirmation = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("Cancel", color = Color.White)
                }
            },
            title = { Text("Confirm Cancel Activity") },
            text = { Text("Are you sure you want to cancel this activity?") },
            containerColor = Color.White
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.wave_border),
                    contentDescription = "Wave Border",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(135.dp),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.BottomCenter
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 150.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
//                    DeviceStatusCard(isWifiOn) { isWifiOn = !isWifiOn }
                    CurrentHydrationStatusCard(
                        healthStats = healthStats,
                        isActivityStarted = isActivityStarted,
                        elapsedTime = elapsedTime,
                        onRefresh = {
                            healthStats = healthStats.copy(last_update = getCurrentDateTime())
                        },
                        onStartClick = {
                            isActivityStarted = true
                            startTime = getCurrentDateTime()
                            elapsedTime = 0L
                            capturedStartStats = healthStats

                            val hydrationLevel = healthStats.hydration_percent.toIntOrNull() ?: -1
                            if (hydrationLevel >= 0) {
//                                sharedViewModel.addHydrationAlert(hydrationLevel)
                            }
                        },
                        onEndClick = { showEndConfirmation = true },
                        onCancelClick = { showCancelConfirmation = true },
                        viewModel = sharedViewModel,
                        heartRateStart = heartRateStart,
                        temperatureStart = temperatureStart,
                        skinConductanceStart = skinConductanceStart,
                        ecgValue = healthStats.ecg_sigmoid
                    )
                }
            }
        }

        ButtonNavigationBar(
            selectedIndex = selectedIndex,
            onItemSelected = { index ->
                selectedIndex = index
                navController.let {
                    when (index) {
                        0 -> Unit
                        1 -> it.navigate("insightsScreen")
                        2 -> it.navigate("alertScreen")
                        3 -> it.navigate("settingsScreen")
                    }
                }
            }
        )
    }
}

@Composable
fun CurrentHydrationStatusCard(
    healthStats: HealthStats,
    isActivityStarted: Boolean,
    elapsedTime: Long,
    onRefresh: () -> Unit,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit,
    onCancelClick: () -> Unit,
    viewModel: SharedViewModel,
    heartRateStart: Int,
    temperatureStart: Double,
    skinConductanceStart: Double,
    ecgValue: String
) {
    var isLoading by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(10.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Current Hydration Status", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Last Update: ${healthStats.last_update}", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            HexagonHydrationIndicator(healthStats.hydration_percent)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Vital Signs", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))

            HealthStatRow(
                "Heart Rate",
                healthStats.heart_rate.toFloatOrNull()?.let { "%.2f bpm".format(it) } ?: "${healthStats.heart_rate} bpm"
            )

            Spacer(modifier = Modifier.height(16.dp))

            HealthStatRow(
                "Body Temperature",
                healthStats.body_temperature.toFloatOrNull()?.let { "%.2f °C".format(it) } ?: "${healthStats.body_temperature} °C"
            )

            Spacer(modifier = Modifier.height(16.dp))

            HealthStatRow(
                "Skin Conductance",
                healthStats.skin_conductance.toFloatOrNull()?.let { "%.2f µS".format(it) } ?: "${healthStats.skin_conductance} µS"
            )

            Spacer(modifier = Modifier.height(16.dp))

            HealthStatRow(
                "ECG",
                healthStats.ecg_sigmoid.toFloatOrNull()?.let { "%.2f mV".format(it) } ?: "${healthStats.ecg_sigmoid} mV"
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isActivityStarted) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Timer: ${elapsedTime / 60}m ${elapsedTime % 60}s",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                if (!isActivityStarted) {
                    Button(
                        onClick = {
                            onStartClick()

                            val sensorList = listOf(
                                SensorData(
                                    heart_rate = heartRateStart.toFloat(),
                                    body_temperature = temperatureStart.toFloat(),
                                    skin_conductance = skinConductanceStart.toFloat(),
                                    ecg_sigmoid = ecgValue.toFloatOrNull() ?: 0f
                                )
                            )

                            viewModel.viewModelScope.launch {
                                try {
                                    val response = RetrofitInstance.authApi.postSensorData(sensorList)
                                    Log.d("SensorSubmit", "Submitted successfully: ${response.message}")
                                } catch (e: Exception) {
                                    Log.e("SensorSubmit", "Failed to submit sensor data", e)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0496E5))
                    ) {
                        Text("Start", color = Color.Black)
                    }
                } else {
                    Button(
                        onClick = onEndClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0496E5))
                    ) {
                        Text("End", color = Color.Black)
                    }
                    Button(
                        onClick = onCancelClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusRow(label: String, status: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.weight(1f))
        Text(status, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
    }
}

@Composable
fun HealthStatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontWeight = FontWeight.Normal, fontSize = 16.sp, color = Color.Black, modifier = Modifier.weight(1f))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

@Composable
fun HexagonHydrationIndicator(hydrationLevel: String) {
    val (percentage, status) = if (hydrationLevel == "N/A") {
        "--" to "N/A"
    } else {
        val value = hydrationLevel.toIntOrNull() ?: -1
        when {
            value in 85..100 -> hydrationLevel to "Hydrated"
            value in 70..84 -> hydrationLevel to "Slightly Dehydrated"
            value in 0..69 -> hydrationLevel to "Dehydrated"
            else -> "--" to "N/A"
        }
    }

    Box(
        modifier = Modifier
            .size(100.dp)
            .wrapContentSize(Alignment.Center),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.bluehexagon_ic),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                percentage,
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(status, color = Color.Black, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}