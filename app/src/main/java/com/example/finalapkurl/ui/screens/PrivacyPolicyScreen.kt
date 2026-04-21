package com.example.finalapkurl.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalapkurl.ui.components.PolicySection

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit = {}
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
                text = "Privacy Policy",
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {

            PolicySection(
                title = "DATA COLLECTION",
                content = "APKURL is designed with privacy at its core. We do not upload your APK files to our servers. All static analysis is performed locally on your device or via secure, ephemeral AI processing."
            )

            PolicySection(
                title = "URL SCANNING",
                content = "When you scan a URL, the link is analyzed for phishing patterns. We do not track your browsing history or store personal information associated with these scans."
            )

            PolicySection(
                title = "AI ANALYSIS",
                content = "Our AI models are trained to detect security threats. While highly accurate, they are not infallible. We recommend using APKURL as a supplementary tool in your security arsenal."
            )

            PolicySection(
                title = "LOCAL STORAGE",
                content = "Your scan history is stored locally on your device. You have full control over this data and can delete it at any time from the History tab."
            )
        }
    }
}
