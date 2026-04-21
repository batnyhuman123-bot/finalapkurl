package com.example.finalapkurl.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalapkurl.data.local.ScanHistoryRecord
import com.example.finalapkurl.ui.components.HistoryItem
import com.example.finalapkurl.ui.components.ScanCard
import com.example.finalapkurl.ui.components.StatItem
import com.example.finalapkurl.ui.util.formatRelativeTime

@Composable
fun HomeScreen(
    totalScans: String,
    highRisks: String,
    lastActivityLabel: String,
    recent: List<ScanHistoryRecord>,
    onScanUrl: () -> Unit,
    onScanApk: () -> Unit,
    onRecentItemClick: (ScanHistoryRecord) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FB))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFE3F2FD), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🔒", color = Color.Black)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "APKURL",
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Security Center",
            color = Color.Black,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            ScanCard("🌐", "Scan URL", onScanUrl)
            ScanCard("📱", "Scan APK", onScanApk)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(20.dp)
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatItem("TOTAL SCANS", totalScans, Color.Blue)
                    StatItem("HIGH RISKS", highRisks, Color.Red)
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(color = Color.LightGray)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Last Scan Activity: $lastActivityLabel",
                    color = Color.Black,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Recent Activity",
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            recent.forEach { entity ->
                HistoryItem(
                    title = entity.title,
                    subtitle = entity.subtitle,
                    risk = entity.riskLabel,
                    time = formatRelativeTime(entity.createdAtMs),
                    isApk = entity.scanType == "APK",
                    onClick = { onRecentItemClick(entity) }
                )
            }
        }
    }
}
