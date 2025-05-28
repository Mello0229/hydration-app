package com.example.application

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
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

@Composable
fun UnitsScreen(navController: NavHostController) {
    var heightUnit by remember { mutableStateOf("FT") }
    var weightUnit by remember { mutableStateOf("LB") }
    var temperatureUnit by remember { mutableStateOf("°F") }
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
                    .height(135.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.wave_border),
                    contentDescription = "Wave Border",
                    modifier = Modifier.fillMaxSize(),
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
                        contentDescription = "Back to Account",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { navController.popBackStack() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Units",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Height", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    UnitToggleButton(
                        text = "FT",
                        isSelected = heightUnit == "FT",
                        onToggle = { heightUnit = "FT" },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    UnitToggleButton(
                        text = "CM",
                        isSelected = heightUnit == "CM",
                        onToggle = { heightUnit = "CM" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Weight", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    UnitToggleButton(
                        text = "LB",
                        isSelected = weightUnit == "LB",
                        onToggle = { weightUnit = "LB" },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    UnitToggleButton(
                        text = "KG",
                        isSelected = weightUnit == "KG",
                        onToggle = { weightUnit = "KG" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Temperature", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    UnitToggleButton(
                        text = "°F",
                        isSelected = temperatureUnit == "°F",
                        onToggle = { temperatureUnit = "°F" },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    UnitToggleButton(
                        text = "°C",
                        isSelected = temperatureUnit == "°C",
                        onToggle = { temperatureUnit = "°C" },
                        modifier = Modifier.weight(1f)
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
fun UnitToggleButton(
    text: String,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) Color(0xFFE0F7FA) else Color.White
    val textColor = if (isSelected) Color(0xFF00ACC1) else Color.Black
    val borderWidth = 1.dp
    val borderColor = Color.LightGray
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .border(borderWidth, borderColor, shape)
            .background(backgroundColor, shape)
            .clickable { onToggle() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = textColor, fontWeight = FontWeight.Medium)
    }
}