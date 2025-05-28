package com.example.application

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.navigation.NavHostController

@Composable
fun TeamManagementScreen(navController: NavHostController) {
    var selectedIndex by remember { mutableStateOf(3) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                    Text(
                        text = "Settings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Team Management",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, bottom = 24.dp)
            )

            ManagementCard(
                emoji = "📋",
                text = "Assigned Athletes → View List",
                onClick = { navController.navigate("viewListScreen") }
            )

            ManagementCard(
                emoji = "➕",
                text = "Add Athlete to Roster",
                onClick = { navController.navigate("addAthleteScreen") }
            )

            ManagementCard(
                emoji = "❌",
                text = "Remove Athlete",
                onClick = { navController.navigate("removeAthleteScreen") }
            )
        }

        CoachNavigationBar(
            selectedIndex = selectedIndex,
            onItemSelected = { index ->
                selectedIndex = index
                when (index) {
                    0 -> navController.navigate("coachHomeScreen")
                    1 -> navController.navigate("athletesScreen")
                    2 -> navController.navigate("coachAlertScreen")
                    3 -> Unit
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ManagementCard(
    emoji: String,
    text: String,
    onClick: () -> Unit,
    textColor: Color = Color.Black
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = emoji,
                fontSize = 14.sp,
                modifier = Modifier.padding(end = 12.dp)
            )
            Text(
                text = text,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}