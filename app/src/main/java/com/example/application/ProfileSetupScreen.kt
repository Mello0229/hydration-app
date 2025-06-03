package com.example.application

import android.app.DatePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.application.models.UserProfile
import com.example.application.network.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.*

@Composable
fun ProfileSetupScreen(navController: NavController) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var sports by remember { mutableStateOf("") }
    var coachAssigned by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var sportsExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val genderOptions = listOf("Male", "Female", "Prefer not to say")
    val sportOptions = listOf(
        "Basketball", "Badminton", "Track and Field", "Soccer", "Futsal",
        "Table Tennis", "Lawn Tennis", "Volleyball", "Combative"
    )

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day -> birthday = "${month + 1}/$day/$year" },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.wave_border),
            contentDescription = "Wave Border",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 180.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Personal Information",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CustomTextField(name, { name = it }, "Full Name")

            OutlinedTextField(
                value = birthday,
                onValueChange = {},
                label = { Text("Birthday (mm/dd/yy)") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE3E3E3),
                    unfocusedContainerColor = Color(0xFFE3E3E3),
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray
                )
            )

            CustomTextField(weight, { weight = it }, "Weight (kg)")

            OutlinedTextField(
                value = gender,
                onValueChange = {},
                label = { Text("Gender") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE3E3E3),
                    unfocusedContainerColor = Color(0xFFE3E3E3),
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray
                )
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                genderOptions.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            gender = it
                            expanded = false
                        }
                    )
                }
            }

            OutlinedTextField(
                value = sports,
                onValueChange = {},
                label = { Text("Sports") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { sportsExpanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE3E3E3),
                    unfocusedContainerColor = Color(0xFFE3E3E3),
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray
                )
            )

            DropdownMenu(
                expanded = sportsExpanded,
                onDismissRequest = { sportsExpanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                sportOptions.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            sports = it
                            sportsExpanded = false
                        }
                    )
                }
            }

            OutlinedTextField(
                value = coachAssigned,
                onValueChange = { coachAssigned = it },
                label = { Text("Coach Assigned") },
                placeholder = { Text("Enter joining code (coach's name)") },
                trailingIcon = {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray)
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE3E3E3),
                    unfocusedContainerColor = Color(0xFFE3E3E3),
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }

            Button(
                onClick = {
                    if (name.isNotBlank() &&
                        birthday.isNotBlank() &&
                        weight.isNotBlank() &&
                        gender.isNotBlank() &&
                        sports.isNotBlank() &&
                        coachAssigned.isNotBlank()
                    ) {
                        scope.launch {
                            try {
                                val profile = UserProfile(
                                    name = name,
                                    dob = birthday,
                                    weight = weight.toFloatOrNull() ?: 0f,
                                    gender = gender.lowercase(),
                                    sport = sports,
                                    coach_name = coachAssigned.trim()
                                )

                                Log.d("AUTH_CHECK", "Token before updateProfile: ${RetrofitInstance.authToken}")
                                Log.d("PROFILE_PAYLOAD", ""+profile)
                                Log.d("SUBMIT_DATA", "coach_name sent: [${coachAssigned.trim()}]")
                                val response = RetrofitInstance.authApi.updateProfile(profile)
                                Log.d("PROFILE_RESPONSE", ""+response)
                                navController.navigate("home")
                            } catch (e: Exception) {
                                Log.e("ERROR_RESPONSE", "Exception during profile update", e)
                                errorMessage = when {
                                    e is HttpException && e.code() == 400 -> "Coach not found or not registered."
                                    else -> "Failed to save profile: ${e.localizedMessage}"
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please fill in all fields before proceeding.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0496E5)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Finish", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@Composable
fun CustomTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFE3E3E3),
            unfocusedContainerColor = Color(0xFFE3E3E3),
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.Gray
        )
    )
}