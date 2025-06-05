package com.example.application

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavHostController
import com.example.application.models.Athlete
import com.example.application.network.RetrofitInstance
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextAlign

@Composable
fun CoachHomeScreen(
    navController: NavHostController
) {
    var athleteList by remember { mutableStateOf<List<Athlete>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitInstance.authApi.getAthletes()
            athleteList = response
        } catch (e: Exception) {
            // Handle error
        }
    }

    val totalAthletes = athleteList.size
    val avgHydration = if (athleteList.isNotEmpty()) athleteList.map { it.hydration_level }.average().toInt() else 0
    val criticalAlerts = athleteList.count { it.status == "Critical" }
    val warningCount = athleteList.count { it.status == "Warning" }
    val hydratedCount = athleteList.count { it.status == "Hydrated" }

    val totalStatus = criticalAlerts + warningCount + hydratedCount
    val proportions = if (totalStatus > 0)
        listOf(
            criticalAlerts.toFloat() / totalStatus,
            warningCount.toFloat() / totalStatus,
            hydratedCount.toFloat() / totalStatus
        ) else listOf(0.33f, 0.33f, 0.34f)

    val pieColors = listOf(Color(0xFFF44336), Color(0xFFFFC107), Color(0xFF00BCD4))

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

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard(
                        title = "Athletes",
                        value = totalAthletes.toString(),
                        backgroundColor = Color(0xFF1C0973),
                        modifier = Modifier
                            .weight(1f)
                            .height(90.dp)
                    )
                    StatCard(
                        title = "Avg. Hydration",
                        value = "$avgHydration%",
                        backgroundColor = Color(0xFF1C0973),
                        modifier = Modifier
                            .weight(1f)
                            .height(90.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard(
                        title = "Critical",
                        value = criticalAlerts.toString(),
                        backgroundColor = Color(0xFFF44336),
                        modifier = Modifier
                            .weight(1f)
                            .height(90.dp)
                    )
                    StatCard(
                        title = "Warning",
                        value = warningCount.toString(),
                        backgroundColor = Color(0xFFFFC107),
                        modifier = Modifier
                            .weight(1f)
                            .height(90.dp)
                    )
                }

                StatCard(
                    title = "Hydrated",
                    value = hydratedCount.toString(),
                    backgroundColor = Color(0xFF00BCD4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                )

//                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Hydration Status Distribution",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                when {
                    totalAthletes == 0 -> {
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .align(Alignment.CenterHorizontally),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hydration data available.",
                                color = Color.Gray,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    totalStatus == 0 -> {
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .align(Alignment.CenterHorizontally),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Hydration data is not available yet.",
                                color = Color.Gray,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    else -> {
                        PieChart(
                            proportions = proportions,
                            colors = pieColors,
                            modifier = Modifier
                                .size(200.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
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

@Composable
fun PieChart(
    proportions: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val canvasSize = size.minDimension
        val radius = canvasSize / 2
        val center = center
        val topLeftOffsetX = (size.width - canvasSize) / 2
        val topLeftOffsetY = (size.height - canvasSize) / 2

        var startAngle = -90f

        proportions.forEachIndexed { index, proportion ->
            val sweep = proportion * 360f

            drawArc(
                color = colors[index],
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = true,
                topLeft = androidx.compose.ui.geometry.Offset(topLeftOffsetX, topLeftOffsetY),
                size = androidx.compose.ui.geometry.Size(canvasSize, canvasSize)
            )

            // Only draw label if it's greater than 1%
            if (proportion > 0.01f) {
                val angle = Math.toRadians((startAngle + sweep / 2).toDouble())
                val labelRadius = radius * 0.6f
                val labelX = (center.x + labelRadius * kotlin.math.cos(angle)).toFloat()
                val labelY = (center.y + labelRadius * kotlin.math.sin(angle)).toFloat()

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        "${(proportion * 100).toInt()}%",
                        labelX,
                        labelY,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 32f
                            isFakeBoldText = true
                        }
                    )
                }
            }
            startAngle += sweep
        }
    }
}