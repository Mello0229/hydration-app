package com.example.application

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import com.example.application.models.TrainingSession
import java.util.*

@Composable
fun InsightsScreen(navController: NavController, sharedViewModel: SharedViewModel) {
    var selectedIndex by remember { mutableStateOf(1) }
    var selectedSession by remember { mutableStateOf<TrainingSession?>(null) }
    var showHideConfirmation by remember { mutableStateOf(false) }
    var sessionToHide by remember { mutableStateOf<TrainingSession?>(null) }

    val activityLogs by sharedViewModel.activityLogs.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {
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
            }

            if (selectedSession != null) {
                DetailedActivityView(session = selectedSession!!) {
                    selectedSession = null
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 72.dp)
                ) {
                    Text(
                        text = "Activity Log",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Total Workouts: ${activityLogs.size}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    if (activityLogs.isEmpty()) {
                        NoActivityMessage()
                    } else {
                        activityLogs.sortedByDescending {
                            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).parse(it.date)
                        }.forEach { session ->
                            var expanded by remember { mutableStateOf(false) }

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                                    .clickable { selectedSession = session },
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(Color.White)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(session.sessionTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            Text(session.time, fontSize = 12.sp, color = Color.Gray)
                                        }
                                        Text(session.date, fontSize = 12.sp, color = Color.Gray)
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.timer_ic),
                                            contentDescription = "Timer Icon",
                                            modifier = Modifier.size(20.dp),
                                            tint = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = session.duration, fontSize = 14.sp)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Hydration Start: ${session.hydrationStart}", fontSize = 14.sp)
                                    Text("Hydration End: ${session.hydrationEnd}", fontSize = 14.sp)

                                    if (!session.description.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Description: ${session.description}", fontSize = 14.sp)
                                    }

                                    if (!session.activity_type.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Activity Type: ${session.activity_type}", fontSize = 14.sp)
                                    }

                                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                                        Box {
                                            IconButton(onClick = { expanded = true }) {
                                                Icon(
                                                    imageVector = Icons.Filled.MoreVert,
                                                    contentDescription = "More options",
                                                    tint = Color.Gray
                                                )
                                            }
                                            DropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false },
                                                modifier = Modifier.background(Color.White)
                                            ) {
                                                DropdownMenuItem(
                                                    text = { Text("Hide Activity") },
                                                    onClick = {
                                                        expanded = false
                                                        sessionToHide = session
                                                        showHideConfirmation = true
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showHideConfirmation) {
            AlertDialog(
                onDismissRequest = { showHideConfirmation = false },
                confirmButton = {
                    Button(
                        onClick = {
                            sessionToHide?.let { sharedViewModel.hideActivityLog(it) }
                            showHideConfirmation = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0496E5))
                    ) {
                        Text("Confirm", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showHideConfirmation = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                },
                title = { Text("Confirm Hide Activity") },
                text = { Text("Are you sure you want to hide this activity?") }
            )
        }

        ButtonNavigationBar(
            selectedIndex = selectedIndex,
            onItemSelected = { index ->
                selectedIndex = index
                when (index) {
                    0 -> navController.navigate("home")
                    1 -> {}
                    2 -> navController.navigate("alertScreen")
                    3 -> navController.navigate("settingsScreen")
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun NoActivityMessage() {
    Text("No activities logged yet.", fontSize = 16.sp, color = Color.Gray)
}

@Composable
fun ActivityLogsList(
    activityLogs: List<TrainingSession>,
    onItemClick: (TrainingSession) -> Unit,
    onHideClick: (TrainingSession) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 72.dp)
    ) {
        activityLogs.forEach { session ->
            var expanded by remember { mutableStateOf(false) }

            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable { onItemClick(session) },
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(session.sessionTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(session.time, fontSize = 12.sp, color = Color.Gray)
                        }
                        Text(session.date, fontSize = 12.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.timer_ic),
                            contentDescription = "Timer Icon",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = session.duration, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Hydration Start: ${session.hydrationStart}", fontSize = 14.sp)
                    Text("Hydration End: ${session.hydrationEnd}", fontSize = 14.sp)

                    if (!session.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Description: ${session.description}", fontSize = 14.sp)
                    }

                    if (!session.activity_type.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Activity Type: ${session.activity_type}", fontSize = 14.sp)
                    }

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = "More options",
                                    tint = Color.Gray
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Hide Activity") },
                                    onClick = {
                                        expanded = false
                                        onHideClick(session)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailedActivityView(session: TrainingSession, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Activity Details", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(12.dp))
                Text("Title: ${session.sessionTitle}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Date: ${session.date}", fontSize = 14.sp)
                Text("Time: ${session.time}", fontSize = 14.sp)
                Text("Duration: ${session.duration}", fontSize = 14.sp)

                Spacer(modifier = Modifier.height(8.dp))
                Text("Hydration Start: ${session.hydrationStart}", fontSize = 14.sp)
                Text("Hydration End: ${session.hydrationEnd}", fontSize = 14.sp)

                Spacer(modifier = Modifier.height(8.dp))
                Text("Heart Rate Start: ${session.heartRateStart} bpm", fontSize = 14.sp)
                Text("Heart Rate End: ${session.heartRateEnd} bpm", fontSize = 14.sp)

                Spacer(modifier = Modifier.height(8.dp))
                Text("Temperature Start: ${session.temperatureStart} °C", fontSize = 14.sp)
                Text("Temperature End: ${session.temperatureEnd} °C", fontSize = 14.sp)

                Spacer(modifier = Modifier.height(8.dp))
                Text("Skin Conductance Start: ${session.skinConductanceStart}", fontSize = 14.sp)
                Text("Skin Conductance End: ${session.skinConductanceEnd}", fontSize = 14.sp)

                Spacer(modifier = Modifier.height(8.dp))
                Text("ECG Start: ${session.ecgStart ?: "N/A"}", fontSize = 14.sp)
                Text("ECG End: ${session.ecgEnd ?: "N/A"}", fontSize = 14.sp)

                if (!session.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Description: ${session.description}", fontSize = 14.sp)
                }

                if (!session.activity_type.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Activity Type: ${session.activity_type}", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0496E5))
                ) {
                    Text("Return", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ActivityStatRow(iconRes: Int, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .padding(end = 8.dp)
        )
        Text(value, fontSize = 14.sp)
    }
}

//data class TrainingSession(
//    val sessionTitle: String,
//    val date: String,
//    val time: String,
//    val duration: String,
//    val hydrationStart: String,
//    val hydrationEnd: String,
//    val hydrationPercentStart: Int,
//    val hydrationPercentEnd: Int,
//    val heartRateStart: Int,
//    val heartRateEnd: Int,
//    val temperatureStart: Double,
//    val temperatureEnd: Double,
//    val skinConductanceStart: Double,
//    val skinConductanceEnd: Double,
//    val ecgStart: Double?,
//    val ecgEnd: Double?,
//    val description: String,
//    val notes: String
//)