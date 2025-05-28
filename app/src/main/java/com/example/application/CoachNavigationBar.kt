package com.example.application

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CoachNavigationBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf("Overview", "Athletes", "Alert", "Settings")
    val icons = listOf(
        R.drawable.overview_ic,
        R.drawable.athlete_ic,
        R.drawable.bell_ic,
        R.drawable.settings
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp),
        color = Color.White,
        shadowElevation = 10.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onItemSelected(index) }
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = icons[index]),
                        contentDescription = item,
                        tint = if (selectedIndex == index) Color(0xFF0496E5) else Color.Gray,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item,
                        fontSize = 11.sp,
                        color = if (selectedIndex == index) Color(0xFF0496E5) else Color.Gray,
                        fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
