package com.example.application

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
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
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun MyWristbandScreen(navController: NavHostController) {
    var selectedIndex by remember { mutableStateOf(3) }

    Box(modifier = Modifier.fillMaxSize()) {
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
                text = "Help",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 135.dp, bottom = 56.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "My Wristband",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f))
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Sample Image Placeholder", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "What is the Wristband LED telling me?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LEDStatusRow(color = "Green", status = "Charged")
            Spacer(modifier = Modifier.height(16.dp))

            LEDStatusRow(color = "Yellow", status = "30% or lower battery")
            Spacer(modifier = Modifier.height(16.dp))

            LEDStatusRow(color = "Red", status = "20% or lower battery")
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "If color above is blinking",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Fast - Your Wristband is ready to use.",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Slow - In workout.",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LEDStatusRow(color = "Blinking Blue", status = "Pairing between Wristband and App.")
            Spacer(modifier = Modifier.height(16.dp))

            LEDStatusRow(color = "Blinking White", status = "Workout interrupted.")
            Spacer(modifier = Modifier.height(24.dp))

            Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

            Text(
                text = "How to take care of your Wristband?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            CareInstruction(instruction = "Gentle clean your wristband and charger case with a damp cloth.")
            CareInstruction(instruction = "Do not submerge your wristband in water.")

            Spacer(modifier = Modifier.height(32.dp))
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
fun LEDStatusRow(color: String, status: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = status,
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun CareInstruction(instruction: String) {
    Text(
        text = "• $instruction",
        fontSize = 16.sp,
        color = Color.Gray,
        modifier = Modifier.padding(bottom = 32.dp)
    )
}