package com.jetcomx.elaina.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jetcomx.elaina.R
import com.jetcomx.elaina.utils.CpuCoreUsage
import top.yukonga.miuix.kmp.basic.Text

@Composable
fun CpuLineChart(usages: List<CpuCoreUsage>, color: Color, modifier: Modifier = Modifier) {
    val axisColor = color.copy(alpha = 0.5f)
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .width(28.dp)
                    .fillMaxHeight()
                    .padding(vertical = 2.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("100%", style = MaterialTheme.typography.labelSmall, color = axisColor, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Text("75%", style = MaterialTheme.typography.labelSmall, color = axisColor, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Text("50%", style = MaterialTheme.typography.labelSmall, color = axisColor, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Text("25%", style = MaterialTheme.typography.labelSmall, color = axisColor, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Text("0%", style = MaterialTheme.typography.labelSmall, color = axisColor, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(2.dp)
            ) {
                val w = size.width; val h = size.height
                if (usages.size < 2) return@Canvas
                val linePath = Path()
                usages.forEachIndexed { i, core ->
                    val x = i.toFloat() / (usages.size - 1) * w
                    val y = h - (core.usagePercent / 100f * h)
                    if (i == 0) linePath.moveTo(x, y) else linePath.lineTo(x, y)
                }
                
                val fillPath = Path().apply {
                    addPath(linePath)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(
                    fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(color.copy(alpha = 0.25f), color.copy(alpha = 0.02f))
                    )
                )
                
                drawPath(linePath, color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
                
                usages.forEachIndexed { i, core ->
                    val x = i.toFloat() / (usages.size - 1) * w
                    val y = h - (core.usagePercent / 100f * h)
                    drawCircle(color, 3.dp.toPx(), Offset(x, y))
                }
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 28.dp, end = 2.dp)
                .height(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            usages.forEachIndexed { i, _ ->
                Text(
                    text = "$i",
                    style = MaterialTheme.typography.labelSmall,
                    color = axisColor,
                    modifier = Modifier.width(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CpuBarChart(usages: List<CpuCoreUsage>, color: Color, modifier: Modifier = Modifier) {
    val coreIndex = stringResource(R.string.core_index)
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        usages.forEach { core ->
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = coreIndex.format(core.coreIndex),
                    style = MaterialTheme.typography.labelSmall,
                    color = color.copy(alpha = 0.7f),
                    modifier = Modifier.width(44.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .background(color.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = (core.usagePercent / 100f).coerceIn(0f, 1f))
                            .background(color, RoundedCornerShape(4.dp))
                    )
                }
                Text(
                    text = "%.0f%%".format(core.usagePercent),
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    modifier = Modifier.width(40.dp).padding(start = 4.dp)
                )
            }
        }
    }
}
