package com.example.finalapkurl.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.finalapkurl.ui.theme.getRiskColor
import com.example.finalapkurl.ui.theme.riskChipBackgroundColorForLabel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScanCard(icon: String, title: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color(0xFFEAF0FF), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(title, color = Color.Black, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            color = color,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryItem(
    title: String,
    subtitle: String,
    risk: String,
    time: String,
    isApk: Boolean = false,
    selectionMode: Boolean = false,
    selected: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) Color(0xFFE3F2FD) else Color.White)
            .then(
                if (onLongClick != null) {
                    Modifier.combinedClickable(onClick = onClick, onLongClick = onLongClick)
                } else {
                    Modifier.clickable(onClick = onClick)
                }
            )
            .padding(16.dp)
    ) {

        Column {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (selectionMode) {
                    Checkbox(
                        checked = selected,
                        onCheckedChange = { onClick() }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isApk) Color(0xFFF3E8FF)
                            else Color(0xFFEAF0FF)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (isApk) "📱" else "🌐")
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {

                    Text(
                        text = title,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(riskChipBackgroundColorForLabel(risk))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = risk,
                        color = getRiskColor(risk),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🕒", fontSize = 12.sp)

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = time,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun PolicySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {

        Text(
            text = title,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = content,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}
