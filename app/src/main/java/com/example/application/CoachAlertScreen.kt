package com.example.application

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
import java.util.Locale

@Composable
fun CoachAlertScreen(navController: NavHostController, viewModel: CoachAlertViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val alerts by viewModel.alerts.collectAsState()
    var selectedSort by remember { mutableStateOf("Date") }
    var selectedIndex by remember { mutableStateOf(2) }

    LaunchedEffect(Unit) {
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
    val color = when (alert.status?.lowercase(Locale.getDefault())) {
        "Critical" -> Color(0xFFD32F2F)
        "Warning" -> Color(0xFFFFA000)
        else -> Color(0xFF00B0FF)
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

                Text(alert.alert_type, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))

                Text(alert.timestamp, fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(alert.description, fontSize = 12.sp)

            if (alert.status?.lowercase(Locale.getDefault()) != "normal") {
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* TODO: resolve alert */ },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("✅ Resolve", fontSize = 12.sp, color = Color.Black)
                    }

                    OutlinedButton(
                        onClick = { /* TODO: dismiss alert */ },
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
                _alerts.value = response
            } catch (e: Exception) {
            }
        }
    }
}