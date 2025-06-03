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
import com.example.application.models.Athlete
import com.example.application.network.RetrofitInstance

@Composable
fun CoachHomeScreen(
    navController: NavHostController
) {
//    val viewModel: SharedViewModel = viewModel()
//    val athleteList by viewModel.athletes.collectAsState()
//
//    LaunchedEffect(Unit) {
//        viewModel.fetchAthletes()
//    }

    var athleteList by remember { mutableStateOf<List<Athlete>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitInstance.authApi.getAthletes()
            athleteList = response
        } catch (e: Exception) {
            // Handle error (log, show toast, etc.)
        }
    }

    val totalAthletes = athleteList.size
    val avgHydration = if (athleteList.isNotEmpty()) athleteList.map { it.hydration_level }.average().toInt() else 0
    val criticalAlerts = athleteList.count { it.status == "Critical" }


    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
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

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Athletes",
                    value = totalAthletes.toString(),
                    backgroundColor = Color(0xFF1C0973),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                StatCard(
                    title = "Avg. Hydration",
                    value = "$avgHydration%",
                    backgroundColor = Color(0xFF1C0973),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                StatCard(
                    title = "Critical Alerts",
                    value = criticalAlerts.toString(),
                    backgroundColor = Color(0xFF1C0973),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
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
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}

@Composable
fun StatCard(title: String, value: String, backgroundColor: Color, modifier: Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(text = title, color = Color.White, fontSize = 14.sp)
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