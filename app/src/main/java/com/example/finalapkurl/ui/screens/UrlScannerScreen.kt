package com.example.finalapkurl.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UrlScannerScreen(
    onBack: () -> Unit = {},
    onScanClick: (String) -> Unit = {}
) {
    var url by remember { mutableStateOf("") }

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
                text = "URL SCANNER",
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Enter a URL below and tap the button to start scanning.",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🌐")

            Spacer(modifier = Modifier.width(10.dp))

            BasicTextField(
                value = url,
                onValueChange = { url = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.Black),
                decorationBox = { innerTextField ->
                    if (url.isEmpty()) {
                        Text(
                            "https://example.com",
                            color = Color.Gray
                        )
                    }
                    innerTextField()
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onScanClick(url) },
            enabled = url.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(55.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (url.isNotBlank())
                    Color(0xFFBBDEFB)
                else
                    Color.LightGray,
                contentColor = if (url.isNotBlank()) Color.Black else Color.DarkGray,
                disabledContentColor = Color.DarkGray
            )
        ) {
            Text(
                text = "Start Scanning",
                color = if (url.isNotBlank()) Color.Black else Color.DarkGray,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
