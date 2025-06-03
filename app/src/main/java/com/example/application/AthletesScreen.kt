package com.example.application

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.application.models.Athlete
import com.example.application.models.Alert
import com.example.application.models.SignUpResponse
import com.example.application.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AthletesScreen(
    navController: NavHostController
//    viewModel: SharedViewModel = viewModel(),
) {
    var selectedSport by remember { mutableStateOf("All Sports") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var selectedAthlete by remember { mutableStateOf<Athlete?>(null) }
    var athleteList by remember { mutableStateOf<List<Athlete>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    val sportOptions = listOf(
        "All Sports",
        "Basketball",
        "Badminton",
        "Track and Field",
        "Soccer",
        "Futsal",
        "Table Tennis",
        "Lawn Tennis",
        "Volleyball",
        "Combative"
    )

    var selectedIndex = 1

    LaunchedEffect(Unit) {
        try {
            athleteList = RetrofitInstance.authApi.getAthletes()
            Log.d("AthletesScreen", "Fetched athlete list: $athleteList")
        } catch (e: Exception) {
            Log.e("AthletesScreen", "Error fetching athletes: ${e.message}")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (selectedAthlete == null) {
            Column(modifier = Modifier.fillMaxSize()) {
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
                    text = "Athletes",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedButton(
                        onClick = { isDropdownExpanded = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                    ) {
                        Text(selectedSport, fontSize = 14.sp, color = Color.Black)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                    }

                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        sportOptions.forEach { sport ->
                            DropdownMenuItem(
                                text = { Text(sport, color = Color.Black) },
                                onClick = {
                                    selectedSport = sport
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search Icon")
                        },
                        placeholder = {
                            Text("Search", fontSize = 14.sp, textAlign = TextAlign.Center)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            unfocusedBorderColor = Color.Gray
                        ),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 14.sp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Name", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("Sport", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text("Hydration", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
                    Text("Status", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                }

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(athleteList) { athlete ->
                        if (athlete != null) {
                            AthleteListItem(athlete = athlete, onClick = { selectedAthlete = it })
                        }
                    }
                }
            }
        } else {
            // ✅ Re-fetch athletes only when selectedAthlete changes
            LaunchedEffect(selectedAthlete) {
                try {
                    athleteList = RetrofitInstance.authApi.getAthletes()
                    Log.d("response", "athleteList (detail): $athleteList")
                } catch (e: Exception) {
                    Log.e("AthletesScreen", "Failed to fetch athletes in detail view: ${e.message}")
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(start = 6.dp, top = 6.dp)
                            .align(Alignment.TopStart)
                    ) {
                        IconButton(onClick = { selectedAthlete = null }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                        Text(
                            text = "Athletes",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                Text(
                    text = "Athlete Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.boyprofile_ic),
                        contentDescription = "Profile",
                        modifier = Modifier.size(80.dp),
                        tint = Color.Black
                    )

                    Text(selectedAthlete!!.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(selectedAthlete!!.sport, fontSize = 14.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text("Hydration Level", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("${selectedAthlete!!.hydration_level}%", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        }
                        StatusChip(status = selectedAthlete!!.status)
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    Text("Vitals", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))

                    Row(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        VitalsCard("${selectedAthlete!!.heart_rate}", "bpm", "Heart Rate")
                        VitalsCard("${selectedAthlete!!.body_temp}", "°C", "Body Temp")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        VitalsCard("${selectedAthlete!!.skin_conductance}", "SC", "Skin Conductance")
                        VitalsCard("${selectedAthlete!!.ecg_sigmoid}", "ecg", "ECG")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Alerts", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                        if (selectedAthlete!!.alerts != null && selectedAthlete!!.alerts.isEmpty()) {
                            Text("No alerts.", modifier = Modifier.align(Alignment.Start))
                        } else if (selectedAthlete!!.alerts != null) {
                            selectedAthlete!!.alerts.forEach { alert ->
                                AthleteAlertItem(text = alert.name, time = alert.timestamp)
                            }
                        }
                    }
                }
            }
        }

        CoachNavigationBar(
            selectedIndex = selectedIndex,
            onItemSelected = { index ->
                selectedIndex = index
                when (index) {
                    0 -> navController.navigate("coachHomeScreen")
                    1 -> {}
                    2 -> navController.navigate("coachAlertScreen")
                    3 -> navController.navigate("coachSettingsScreen")
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 0.dp)
        )
    }
}

@Composable
fun AthleteListItem(athlete: Athlete, onClick: (Athlete) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(athlete) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Log.d("response", "athlete: $athlete")
        Text(athlete.name, modifier = Modifier.weight(1f))
        Text(athlete.sport, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text("${athlete.hydration_level}%", modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
        StatusChip(status = athlete.status, modifier = Modifier.weight(1f))
    }
//    Log.d("response", "athlete: $athlete")
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { onClick(athlete) }
//            .padding(horizontal = 16.dp, vertical = 12.dp),
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Log.d("response", "athlete: $athlete")
//        Text(athlete.name, modifier = Modifier.weight(1f))
//        Text(athlete.sport, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
//        Text("${athlete.hydration}%", modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
//        StatusChip(status = athlete.status, modifier = Modifier.weight(1f))
//    }
}

@Composable
fun StatusChip(status: String, modifier: Modifier = Modifier) {
    val backgroundColor = when (status) {
        "Hydrated" -> Color(0xFF00BCD4)
        "Warning" -> Color(0xFFFFC107)
        "Critical" -> Color(0xFFF44336)
        else -> Color.Gray
    }

    Box(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .background(backgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(status, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun VitalsCard(value: String, unit: String, label: String) {
    Column(
        modifier = Modifier
            .background(Color(0xFF001F5B), shape = RoundedCornerShape(8.dp))
            .size(width = 150.dp, height = 80.dp)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("$value $unit", fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, color = Color.White)
    }
}

@Composable
fun AthleteAlertItem(text: String, time: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
        Text(time, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.align(Alignment.Start))
    }
}