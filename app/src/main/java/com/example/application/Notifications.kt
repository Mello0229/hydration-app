package com.example.application

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material3.Icon
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SwitchDefaults

@Composable
fun NotificationsScreen(navController: NavHostController) {
    var pushHydrationNotificationEnabled by remember { mutableStateOf(false) }
    var pushTransferringDataNotificationEnabled by remember { mutableStateOf(false) }
    var watchStartWorkoutNotificationEnabled by remember { mutableStateOf(false) }
    var watchHydrationNotificationEnabled by remember { mutableStateOf(false) }
    var watchEndWorkoutNotificationEnabled by remember { mutableStateOf(false) }
    var watchTransferringDataNotificationEnabled by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(3) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(bottom = 56.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { navController.popBackStack() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Settings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                item {
                    Text(
                        text = "Notifications",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    Text("Push", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    NotificationItem(
                        title = "Hydration notifications",
                        description = "Notify me when I need to replenish",
                        isEnabled = pushHydrationNotificationEnabled,
                        onEnabledChange = { pushHydrationNotificationEnabled = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    NotificationItem(
                        title = "Transferring Data",
                        description = "Notify me when a workout transferring is finished",
                        isEnabled = pushTransferringDataNotificationEnabled,
                        onEnabledChange = { pushTransferringDataNotificationEnabled = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    Text("Watch", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    NotificationItem(
                        title = "Start Workout",
                        description = "Notify me when the workout starts",
                        isEnabled = watchStartWorkoutNotificationEnabled,
                        onEnabledChange = { watchStartWorkoutNotificationEnabled = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    NotificationItem(
                        title = "Hydration notifications",
                        description = "Notify me when I need to replenish",
                        isEnabled = watchHydrationNotificationEnabled,
                        onEnabledChange = { watchHydrationNotificationEnabled = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    NotificationItem(
                        title = "End Workout",
                        description = "Notify me when the workout ends",
                        isEnabled = watchEndWorkoutNotificationEnabled,
                        onEnabledChange = { watchEndWorkoutNotificationEnabled = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    NotificationItem(
                        title = "Transferring Data",
                        description = "Notify me when a workout transferring is finished",
                        isEnabled = watchTransferringDataNotificationEnabled,
                        onEnabledChange = { watchTransferringDataNotificationEnabled = it }
                    )
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
                    2 -> navController.navigate("alertScreen")
                    3 -> Unit
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun NotificationItem(
    title: String,
    description: String,
    isEnabled: Boolean,
    onEnabledChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(description, color = Color.Gray, fontSize = 14.sp)
            }
            Switch(
                checked = isEnabled,
                onCheckedChange = onEnabledChange,
                enabled = true,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF0496E5),
                    uncheckedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF82CFFF),
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
    }
}