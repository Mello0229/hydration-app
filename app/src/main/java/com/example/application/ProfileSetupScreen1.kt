package com.example.application

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavController
import com.example.application.models.CoachProfile
import com.example.application.models.UserProfile
import com.example.application.network.RetrofitInstance
import kotlinx.coroutines.launch

@Composable
fun ProfileSetupScreen1(navController: NavController) {
    var coachName by remember { mutableStateOf("") }
    var sportsManage by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val sportsOptions = listOf(
        "Basketball", "Badminton", "Track and Field", "Soccer", "Futsal",
        "Table Tennis", "Lawn Tennis", "Volleyball", "Combative"
    )
    var sportsExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.wave_border),
            contentDescription = "Wave Design",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Personal Information",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomInputField(value = coachName, onValueChange = { coachName = it }, label = "Coach Full Name")

        OutlinedTextField(
            value = sportsManage,
            onValueChange = {},
            label = { Text("Sports Manage") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { sportsExpanded = true }) {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select Sport")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE3E3E3),
                unfocusedContainerColor = Color(0xFFE3E3E3),
                disabledContainerColor = Color(0xFFE3E3E3),
                cursorColor = Color.Black
            )
        )

        DropdownMenu(
            expanded = sportsExpanded,
            onDismissRequest = { sportsExpanded = false },
            modifier = Modifier
                .background(Color.White)
        ) {
            sportsOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        sportsManage = option
                        sportsExpanded = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        CustomInputField(value = email, onValueChange = { email = it }, label = "Email")
        CustomInputField(value = contactNumber, onValueChange = { contactNumber = it }, label = "Contact Number")

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { /* Navigate to View Athletes */ },
            colors = ButtonDefaults.buttonColors(Color(0xFF0496E5)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(50.dp),
            shape = RoundedCornerShape(50)
        ) {
            Text(text = "View my Athletes", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(5.dp))
        }

        Button(
            onClick = {
                if (coachName.isNotBlank() && sportsManage.isNotBlank() && email.isNotBlank()) {
                    scope.launch {
                        try {
                            val profile = CoachProfile(
                                full_name = coachName,
                                sport = sportsManage,
                                email = email,
                                contact = contactNumber
                            )
                            Log.d("PROFILE_PAYLOAD", ""+profile)
                            val response = RetrofitInstance.authApi.updateCoachProfile(profile)
                            Log.d("PROFILE_RESPONSE", ""+response)
                            navController.navigate("coachHomeScreen")
                        } catch (e: Exception) {
                            Log.e("ERROR_RESPONSE", "",e)
                            errorMessage = "Failed to save profile: ${e.localizedMessage}"
                        }
                    }
                } else {
                    errorMessage = "Please fill in all required fields."
                }
            },
            colors = ButtonDefaults.buttonColors(Color(0xFF0496E5)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(50.dp),
            shape = RoundedCornerShape(50)
        ) {
            Text(text = "Finish", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun CustomInputField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFE3E3E3),
            unfocusedContainerColor = Color(0xFFE3E3E3),
            disabledContainerColor = Color(0xFFE3E3E3),
            cursorColor = Color.Black
        )
    )

    Spacer(modifier = Modifier.height(10.dp))
}