package com.example.finalapkurl.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.finalapkurl.ui.theme.getRiskColor
import com.example.finalapkurl.ui.theme.riskResultScoreBoxColor
import com.example.finalapkurl.ui.theme.riskResultScreenBackgroundTint
import com.example.finalapkurl.ui.theme.riskTierFromLabel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResultScreen(
    riskLevel: String = "Low Risk",
    riskScore: Int = 15,
    url: String = "https://secure-login.net",
    summary: String = "",
    reason: String = "",
    onDone: () -> Unit = {}
) {

    val tier = riskTierFromLabel(riskLevel)
    val bgColor = riskResultScreenBackgroundTint(tier)
    val scoreBoxColor = riskResultScoreBoxColor(tier)

    val reasonText = if (reason.isNotBlank()) reason else when (riskLevel) {
        "Low Risk" -> "Verified Safe"
        "Medium Risk" -> "Suspicious Activity"
        else -> "Malicious Detected"
    }

    val summaryText = if (summary.isNotBlank()) summary else when (riskLevel) {
        "Low Risk" ->
            "Analysis complete. This URL appears to be safe based on our current security database and heuristic checks."

        "Medium Risk" ->
            "This URL shows some suspicious indicators. Proceed with caution and avoid entering sensitive information."

        else ->
            "Warning! This URL is flagged as potentially malicious. It may contain phishing or harmful content."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FB))
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "REPORT",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Text(
                text = "Done",
                color = Color(0xFF4A6CF7),
                modifier = Modifier.clickable { onDone() }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(bgColor)
                .padding(vertical = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    text = riskLevel,
                    color = Color.Black,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(scoreBoxColor)
                        .padding(horizontal = 24.dp, vertical = 14.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "RISK SCORE",
                            color = Color.Black,
                            fontSize = 12.sp
                        )

                        Text(
                            text = "$riskScore%",
                            color = Color.Black,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "ANALYSIS SUMMARY",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = summaryText,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Column {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("TARGET URL", color = Color.Gray, fontSize = 12.sp)
                    Text(url, color = Color.Black, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(10.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("REASON", color = Color.Gray, fontSize = 12.sp)

                    Text(
                        text = reasonText,
                        fontWeight = FontWeight.Medium,
                        color = getRiskColor(riskLevel)
                    )
                }
            }
        }
    }
}
