package com.kasirku.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kasirku.app.ui.theme.TealPrimary
import com.kasirku.app.viewmodel.KasirViewModel

@Composable
fun SalesChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth().height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Belum ada data penjualan",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        return
    }

    val maxValue = data.maxOf { it.second }.coerceAtLeast(1.0)
    val barColor = TealPrimary
    val gridColor = MaterialTheme.colorScheme.outlineVariant

    Column(modifier = modifier) {
        // Chart
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(horizontal = 4.dp)
        ) {
            val chartWidth = size.width
            val chartHeight = size.height
            val barCount = data.size
            val spacing = chartWidth * 0.08f
            val barWidth = (chartWidth - spacing * (barCount + 1)) / barCount

            // Grid lines
            for (i in 0..4) {
                val y = chartHeight * (1f - i / 4f)
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(chartWidth, y),
                    strokeWidth = 1f
                )
            }

            // Bars
            data.forEachIndexed { index, (_, value) ->
                val barHeight = (value / maxValue * chartHeight * 0.9f).toFloat()
                val x = spacing + index * (barWidth + spacing)
                val y = chartHeight - barHeight

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }
        }

        // Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach { (label, _) ->
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Value labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach { (_, value) ->
                Text(
                    text = KasirViewModel.formatRupiah(value).replace("Rp ", ""),
                    fontSize = 9.sp,
                    color = TealPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
