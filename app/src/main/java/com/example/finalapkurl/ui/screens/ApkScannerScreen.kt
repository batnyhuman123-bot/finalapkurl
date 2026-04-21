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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalapkurl.ui.modifier.dashedBorder

@Composable
fun ApkScannerScreen(
    onBack: () -> Unit = {},
    onSelectApk: () -> Unit = {},
    onScanClick: () -> Unit = {},
    hasFileSelected: Boolean = false,
    fileDisplayName: String = "Choose APK File"
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FB))
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "←",
                fontSize = 20.sp,
                modifier = Modifier.clickable { onBack() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "APK SCANNER",
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(220.dp)
                .dashedBorder()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .clickable { onSelectApk() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFFF0F2F5), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⬆", fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = fileDisplayName,
                    color = Color.Black,
                    fontWeight = if (hasFileSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 16.sp
                )

                Text(
                    text = "or Drag & Drop",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { onScanClick() },
            enabled = hasFileSelected,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(55.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (hasFileSelected) Color(0xFFBBDEFB) else Color.LightGray,
                contentColor = if (hasFileSelected) Color.Black else Color.DarkGray,
                disabledContentColor = Color.DarkGray
            )
        ) {
            Text(
                text = "Start Scanning",
                color = if (hasFileSelected) Color.Black else Color.DarkGray,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
