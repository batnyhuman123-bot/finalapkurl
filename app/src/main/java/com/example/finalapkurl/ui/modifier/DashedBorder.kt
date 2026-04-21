package com.example.finalapkurl.ui.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.dashedBorder(
    color: Color = Color.Gray,
    cornerRadius: Dp = 20.dp,
    dashOn: Float = 10f,
    dashOff: Float = 10f,
    strokeWidth: Dp = 2.dp
): Modifier = this.drawWithContent {
    drawContent()
    val w = strokeWidth.toPx()
    drawRoundRect(
        color = color,
        style = Stroke(
            width = w,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashOn, dashOff))
        ),
        cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
    )
}
