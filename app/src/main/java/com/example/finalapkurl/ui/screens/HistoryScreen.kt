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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalapkurl.data.local.ScanHistoryRecord
import com.example.finalapkurl.ui.components.HistoryItem
import com.example.finalapkurl.ui.util.formatRelativeTime

@Composable
fun HistoryScreen(
    items: List<ScanHistoryRecord>,
    onConfirmClearAll: () -> Unit,
    onItemClick: (ScanHistoryRecord) -> Unit = {},
    onDeleteSelected: (Set<Long>) -> Unit = {}
) {
    var showClearDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf<Set<Long>>(emptySet()) }

    fun exitSelectionMode() {
        isSelectionMode = false
        selectedIds = emptySet()
    }

    fun toggleSelect(id: Long) {
        selectedIds = if (id in selectedIds) selectedIds - id else selectedIds + id
        if (selectedIds.isEmpty()) isSelectionMode = false
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Delete Selected Items?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            },
            text = {
                Text(
                    text = "This will permanently delete the selected scan records. This action cannot be undone.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Start,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteSelected(selectedIds)
                        showDeleteDialog = false
                        exitSelectionMode()
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Delete",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = false },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE0E0E0),
                        contentColor = Color.DarkGray
                    )
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Clear History?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            },
            text = {
                Text(
                    text = "This will permanently delete all your scan records. This action cannot be undone.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Start,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirmClearAll()
                        showClearDialog = false
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFCDD2),
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Yes, Clear All",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { showClearDialog = false },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE8E8E8),
                        contentColor = Color.DarkGray
                    )
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FB))
            .padding(16.dp)
    ) {

        if (isSelectionMode) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cancel",
                    color = Color(0xFF4A6CF7),
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { exitSelectionMode() }
                )
                Text(
                    text = "${selectedIds.size} selected",
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Delete",
                    color = if (selectedIds.isNotEmpty()) Color.Red else Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(enabled = selectedIds.isNotEmpty()) {
                        showDeleteDialog = true
                    }
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HISTORY",
                    color = Color.Black,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFEBEE))
                        .clickable { showClearDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text("🗑️")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items, key = { it.id }) { entity ->
                val id = entity.id
                val selected = id in selectedIds
                HistoryItem(
                    title = entity.title,
                    subtitle = entity.subtitle,
                    risk = entity.riskLabel,
                    time = formatRelativeTime(entity.createdAtMs),
                    isApk = entity.scanType == "APK",
                    selectionMode = isSelectionMode,
                    selected = selected,
                    onClick = {
                        if (isSelectionMode) toggleSelect(id)
                        else onItemClick(entity)
                    },
                    onLongClick = {
                        if (!isSelectionMode) {
                            isSelectionMode = true
                            selectedIds = setOf(id)
                        }
                    }
                )
            }
        }
    }
}
