package com.example.finalapkurl.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    onPrivacyClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FB))
            .padding(16.dp)
    ) {

        Text(
            text = "Settings",
            color = Color.Black,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SUPPORT & INFO",
            color = Color.Gray,
            fontSize = 12.sp,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable { onPrivacyClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text("🛡️", fontSize = 18.sp)

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Privacy Policy",
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            Text(">", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFEAF0FF))
                .padding(16.dp)
        ) {
            Text(
                text = "This app helps you check if a link or APK might be dangerous, but it may not always be correct. Always stay careful when opening links or installing apps. The final choice is yours.",
                color = Color.Black,
                fontSize = 14.sp
            )
        }
    }
}
