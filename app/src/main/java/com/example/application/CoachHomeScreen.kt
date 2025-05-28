package com.example.application

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun CoachHomeScreen(
    navController: NavHostController,
    viewModel: SharedViewModel = viewModel()
) {
    val athletes by viewModel.athletes.collectAsState()

    val totalAthletes = athletes.size
    val avgHydration = if (athletes.isNotEmpty()) athletes.map { it.hydration_level }.average().toInt() else 0
    val criticalAlerts = athletes.count { it.status == "Critical" }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Image(
            painter = painterResource(id = R.drawable.wave_border),
            contentDescription = "Wave Border",
            modifier = Modifier
                .fillMaxWidth()
                .height(135.dp),
            contentScale = ContentScale.Crop,
            alignment = Alignment.BottomCenter
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Coach Dashboard",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatCard(title = "Athletes", value = totalAthletes.toString(), backgroundColor = Color(0xFF1C0973))
            StatCard(title = "Avg. Hydration", value = "$avgHydration%", backgroundColor = Color(0xFF1C0973))
            StatCard(title = "Critical Alerts", value = criticalAlerts.toString(), backgroundColor = Color(0xFF1C0973))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Athlete Summary",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            items(athletes.sortedByDescending { it.status == "Critical" }) { athlete ->
                AthleteHydrationRow(
                    name = athlete.name,
                    hydration = "${athlete.hydration_level}%",
                    status = athlete.status,
                    color = when (athlete.status) {
                        "Critical" -> Color(0xFFD32F2F)
                        "Warning" -> Color(0xFFFFA000)
                        else -> Color(0xFF00BCD4)
                    }
                )
            }
        }

        CoachNavigationBar(
            selectedIndex = 0,
            onItemSelected = { index ->
                when (index) {
                    0 -> {}
                    1 -> navController.navigate("athletesScreen")
                    2 -> navController.navigate("coachAlertScreen")
                    3 -> navController.navigate("coachSettingsScreen")
                }
            }
        )
    }
}

@Composable
fun StatCard(title: String, value: String, backgroundColor: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.size(width = 100.dp, height = 80.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = title, color = Color.White, fontSize = 12.sp)
        }
    }
}

@Composable
fun AthleteHydrationRow(name: String, hydration: String, status: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(id = R.drawable.boyprofile_ic),
            contentDescription = null,
            modifier = Modifier.size(36.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.Bold)
            val percentage = hydration.substringBefore(" ")
            val remainingText = hydration.substringAfter(" ")
            Row {
                Text(percentage, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(remainingText, fontSize = 12.sp, color = Color.Gray)
            }
        }
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = color),
            shape = RoundedCornerShape(30),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            modifier = Modifier.size(width = 100.dp, height = 36.dp)
        ) {
            Text(text = status, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}