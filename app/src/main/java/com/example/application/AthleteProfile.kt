package com.example.application

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun AthleteProfileScreen(navController: NavHostController) {
    val athlete = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<Athlete>("selectedAthlete") ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
            text = "Athlete Profile",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.boyprofile_ic),
                contentDescription = "Profile",
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = athlete.name,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = athlete.sport,
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Hydration Level", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            StatusChip(
                status = athlete.status,
                modifier = Modifier.wrapContentWidth()
            )
        }

        Text(
            text = "${athlete.hydration}%",
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            modifier = Modifier
                .padding(start = 16.dp)
                .padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Divider()

        Text(
            text = "Vitals",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp)
        )

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                VitalsCard("145", "bpm", "Heart Rate", Modifier.weight(1f))
                VitalsCard("37", "°C", "Body Temp", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                VitalsCard("0.25", "SC", "Skin Conductance", Modifier.weight(1f))
                VitalsCard("50", "H", "Humidity", Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Alerts",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        AlertItem("Dehydration risk detected", "Today, 09:20 am")
        AlertItem("Dehydration risk detected", "Today, 03:00 pm")
    }
}

@Composable
fun VitalsCard(value: String, unit: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFF00008B), RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$value$unit",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.White
        )
        Text(text = label, fontSize = 12.sp, color = Color.White)
    }
}

@Composable
fun AlertItem(message: String, time: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Text(
            text = "🚨",
            fontSize = 24.sp,
            modifier = Modifier.padding(end = 8.dp)
        )
        Column {
            Text(text = message, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(text = time, fontSize = 12.sp, color = Color.Gray)
        }
    }
}