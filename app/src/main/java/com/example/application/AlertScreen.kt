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

@Composable
fun AlertScreen(navController: NavController) {
    var selectedIndex by remember { mutableStateOf(2) }

    val newAlerts = remember { mutableStateListOf<HydrationAlert>() }
    val earlierAlerts = remember { mutableStateListOf<HydrationAlert>() }

//    LaunchedEffect(Unit) {
//        if (newAlerts.isEmpty() && earlierAlerts.isEmpty()) {
//            newAlerts.addAll(
//                listOf(
//                    HydrationAlert(
//                        id = "1",
//                        title = "Hydration Warning",
//                        message = "You are slightly dehydrated. Drink 250ml of water to maintain optimal performance.",
//                        type = AlertType.WARNING,
//                        timestamp = "now"
//                    ),
//                    HydrationAlert(
//                        id = "2",
//                        title = "Critical Hydration Alert",
//                        message = "You are in a dehydrated state! Immediate hydration is recommended to prevent fatigue and performance decline.",
//                        type = AlertType.CRITICAL,
//                        timestamp = "now"
//                    ),
//                    HydrationAlert(
//                        id = "3",
//                        title = "Daily Hydration Goal Reminder",
//                        message = "You’ve consumed 1.5L of water today. Keep going!\nYour daily hydration goal is 2.5L.",
//                        type = AlertType.REMINDER,
//                        timestamp = "now"
//                    )
//                )
//            )
//            earlierAlerts.addAll(
//                listOf(
//                    HydrationAlert(
//                        id = "4",
//                        title = "Daily Hydration Goal Reminder",
//                        message = "You’ve consumed 1.5L of water today. Keep going!\nYour daily hydration goal is 2.5L.",
//                        type = AlertType.REMINDER,
//                        timestamp = "earlier"
//                    ),
//                    HydrationAlert(
//                        id = "5",
//                        title = "Daily Hydration Goal Reminder",
//                        message = "You’ve consumed 1.5L of water today. Keep going!\nYour daily hydration goal is 2.5L.",
//                        type = AlertType.REMINDER,
//                        timestamp = "earlier"
//                    ),
//                    HydrationAlert(
//                        id = "6",
//                        title = "Critical Hydration Alert",
//                        message = "You are in a dehydrated state! Immediate hydration is recommended to prevent fatigue and performance decline.",
//                        type = AlertType.CRITICAL,
//                        timestamp = "earlier"
//                    ),
//                    HydrationAlert(
//                        id = "7",
//                        title = "Hydration Warning",
//                        message = "You are slightly dehydrated. Drink 250ml of water to maintain optimal performance.",
//                        type = AlertType.WARNING,
//                        timestamp = "earlier"
//                    )
//                )
//            )
//        }
//    }

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

@Composable
fun AlertCard(alert: HydrationAlert) {
    val icon = when (alert.type) {
        AlertType.CRITICAL -> "🚨" // 🚨
        AlertType.WARNING -> "⚠️" // ⚠️
        AlertType.REMINDER -> "💧" // 💧
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

data class HydrationAlert(
    val id: String,
    val title: String,
    val message: String,
    val type: AlertType,
    val timestamp: String
)

enum class AlertType {
    CRITICAL,
    WARNING,
    REMINDER
}