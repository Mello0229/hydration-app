package com.example.application

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.*

data class HealthStats(
    val heart_rate: String = "N/A",
    val body_temp: String = "N/A",
    val skin_conductance: String = "N/A",
    val hydration_level: String = "N/A",
    val ecg_sigmoid: String = "N/A",
    val last_update: String = getCurrentDateTime()
)

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
    var hydrationStart by remember { mutableStateOf(0) }
    var hydrationEnd by remember { mutableStateOf(0) }
    var heartRateStart by remember { mutableStateOf(0) }
    var heartRateEnd by remember { mutableStateOf(0) }
    var temperatureStart by remember { mutableStateOf(0.0) }
    var temperatureEnd by remember { mutableStateOf(0.0) }
    var skinConductanceStart by remember { mutableStateOf(0.0) }
    var skinConductanceEnd by remember { mutableStateOf(0.0) }
    var showActivityForm by remember { mutableStateOf(false) }
    var showEndConfirmation by remember { mutableStateOf(false) }
    var showCancelConfirmation by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var isBluetoothOn by remember { mutableStateOf(false) }
    var healthStats by remember { mutableStateOf(HealthStats()) }
    var selectedIndex by remember { mutableStateOf(0) }

    LaunchedEffect(isActivityStarted) {
        while (isActivityStarted) {
            kotlinx.coroutines.delay(1000)
            elapsedTime += 1
        }
    }

    if (showActivityForm) {
        AlertDialog(
            onDismissRequest = { showActivityForm = false },
            confirmButton = {
                Button(
                    onClick = {
                        val session = TrainingSession(
                            sessionTitle = activityTitle,
                            date = getCurrentDateTime().split(" - ")[0],
                            time = getCurrentDateTime().split(" - ")[1],
                            duration = "${elapsedTime / 60}m ${elapsedTime % 60}s",
                            hydrationStart = hydrationStart.toString(),
                            hydrationEnd = hydrationEnd.toString(),
                            hydrationPercentStart = hydrationStart,
                            hydrationPercentEnd = hydrationEnd,
                            heartRateStart = heartRateStart,
                            heartRateEnd = heartRateEnd,
                            temperatureStart = temperatureStart,
                            temperatureEnd = temperatureEnd,
                            skinConductanceStart = skinConductanceStart,
                            skinConductanceEnd = skinConductanceEnd,
                            ecgStart = healthStats.ecg_sigmoid.toDoubleOrNull() ?: 0.0,
                            ecgEnd = healthStats.ecg_sigmoid.toDoubleOrNull() ?: 0.0,
                            description = activityDescription,
                            notes = activityType
                        )
                        sharedViewModel.addActivityLog(session)
                        showActivityForm = false
                        isActivityStarted = false
                        elapsedTime = 0L
                        activityTitle = ""
                        activityType = ""
                        activityDescription = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Finish", color = Color.White)
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
                        endTime = getCurrentDateTime()
                        isActivityStarted = false
                        showActivityForm = true
                        showEndConfirmation = false
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
                    DeviceStatusCard(isBluetoothOn) { isBluetoothOn = !isBluetoothOn }
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
                        },
                        onEndClick = { showEndConfirmation = true },
                        onCancelClick = { showCancelConfirmation = true }
                    )
                    Spacer(modifier = Modifier.height(80.dp))
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
    onCancelClick: () -> Unit
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

            HexagonHydrationIndicator(healthStats.hydration_level)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Vital Signs", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(16.dp))

            HealthStatRow("Heart Rate", healthStats.heart_rate)
            HealthStatRow("Body Temperature", healthStats.body_temp)
            HealthStatRow("Skin Conductance", healthStats.skin_conductance)
            HealthStatRow("ECG", healthStats.ecg_sigmoid) // ✅ Added ECG display

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
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(enabled = !isLoading) {
                            isLoading = true
                            onRefresh()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        LaunchedEffect(Unit) {
                            kotlinx.coroutines.delay(1500)
                            isLoading = false
                        }
                        CircularProgressIndicator(
                            color = Color.Black,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                if (!isActivityStarted) {
                    Button(onClick = onStartClick, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0496E5))) {
                        Text("Start", color = Color.Black)
                    }
                } else {
                    Button(onClick = onEndClick, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0496E5))) {
                        Text("End", color = Color.Black)
                    }
                    Button(onClick = onCancelClick, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                        Text("Cancel", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceStatusCard(isWifiOn: Boolean, onWifiToggle: () -> Unit) {
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
            Text("Device Status", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            StatusRow("WiFi", status = if (isWifiOn) "On" else "Off", onClick = { onWifiToggle() })
            Spacer(modifier = Modifier.height(12.dp))
            StatusRow("Wristband", status = "Not Paired")
            Spacer(modifier = Modifier.height(12.dp))
            StatusRow("Battery", status = "Not Paired")
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
        // Removed icon
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
        Text(label, fontWeight = FontWeight.Normal, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.weight(1f))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
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