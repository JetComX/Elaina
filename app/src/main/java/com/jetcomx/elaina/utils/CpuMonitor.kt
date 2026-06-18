package com.jetcomx.elaina.utils

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

object CpuMonitor {
    private const val TAG = "CpuMonitor"
    private const val SMA_WINDOW = 4

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _cpuUsages = MutableStateFlow<List<CpuCoreUsage>>(emptyList())
    val cpuUsages: StateFlow<List<CpuCoreUsage>> = _cpuUsages.asStateFlow()

    private var previousSnapshot: Map<Int, LongArray>? = null
    private val smaHistory = mutableMapOf<Int, ArrayDeque<Float>>()

    fun startMonitoring(fastMode: Boolean = false) {
        scope.launch {
            val initDelay = if (fastMode) 500L else 2500L
            val sampleDelay = if (fastMode) 100L else 500L
            val pollInterval = if (fastMode) 200L else 500L

            Log.i(TAG, "CPU 监控启动（fast=$fastMode），采集基线...")
            previousSnapshot = readCpuStats()

            val samples = mutableListOf<Map<Int, RawUsage>>()
            delay(initDelay)
            samples.add(sampleRawUsage())
            delay(sampleDelay)
            samples.add(sampleRawUsage())
            delay(sampleDelay)
            samples.add(sampleRawUsage())

            emitInitialAveraged(samples)
            Log.i(TAG, "首轮 CPU 数据已就绪（3 次采样平均，fast=$fastMode）")

            while (true) {
                delay(pollInterval)
                updateWithSMA()
            }
        }
    }

    private fun sampleRawUsage(): Map<Int, RawUsage> {
        val current = readCpuStats()
        val prev = previousSnapshot
        previousSnapshot = current
        val result = mutableMapOf<Int, RawUsage>()
        if (prev == null) return result
        for ((coreIndex, times) in current) {
            val pt = prev[coreIndex] ?: continue
            val totalDelta = times.sum() - pt.sum()
            val idleDelta = (times[3] + times[4]) - (pt[3] + pt[4])
            val usage = if (totalDelta > 0) {
                ((totalDelta - idleDelta).toFloat() / totalDelta * 100f).coerceIn(0f, 100f)
            } else 0f
            result[coreIndex] = RawUsage(
                usagePercent = usage,
                user = times[0] - pt[0],
                nice = times[1] - pt[1],
                system = times[2] - pt[2],
                idle = times[3] - pt[3],
                iowait = times[4] - pt[4],
                irq = times[5] - pt[5],
                softirq = times[6] - pt[6],
                totalDelta = totalDelta
            )
        }
        return result
    }

    private fun emitInitialAveraged(samples: List<Map<Int, RawUsage>>) {
        val allCores = samples.flatMap { it.keys }.toSet()
        _cpuUsages.value = allCores.map { coreIndex ->
            val usages = samples.mapNotNull { it[coreIndex] }
            if (usages.isEmpty()) return@map CpuCoreUsage(coreIndex, 0f)
            val avgUsage = usages.map { it.usagePercent }.average().toFloat()
            val window = ArrayDeque<Float>()
            repeat(SMA_WINDOW) { window.addLast(avgUsage) }
            smaHistory[coreIndex] = window
            CpuCoreUsage(
                coreIndex = coreIndex,
                usagePercent = avgUsage,
                user = usages.map { it.user }.average().toLong(),
                nice = usages.map { it.nice }.average().toLong(),
                system = usages.map { it.system }.average().toLong(),
                idle = usages.map { it.idle }.average().toLong(),
                iowait = usages.map { it.iowait }.average().toLong(),
                irq = usages.map { it.irq }.average().toLong(),
                softirq = usages.map { it.softirq }.average().toLong(),
                totalDelta = usages.map { it.totalDelta }.average().toLong()
            )
        }.sortedBy { it.coreIndex }
    }

    private fun updateWithSMA() {
        try {
            val current = readCpuStats()
            val prev = previousSnapshot
            previousSnapshot = current
            if (prev == null) return

            _cpuUsages.value = current.map { (coreIndex, times) ->
                val pt = prev[coreIndex]
                if (pt == null) return@map CpuCoreUsage(coreIndex, 0f)
                val totalDelta = times.sum() - pt.sum()
                val idleDelta = (times[3] + times[4]) - (pt[3] + pt[4])
                val usage = if (totalDelta > 0) {
                    ((totalDelta - idleDelta).toFloat() / totalDelta * 100f).coerceIn(0f, 100f)
                } else 0f
                val window = smaHistory.getOrPut(coreIndex) { ArrayDeque() }
                window.addLast(usage)
                if (window.size > SMA_WINDOW) window.removeFirst()
                val smoothed = window.average().toFloat()

                CpuCoreUsage(
                    coreIndex = coreIndex,
                    usagePercent = smoothed,
                    user = times[0] - pt[0],
                    nice = times[1] - pt[1],
                    system = times[2] - pt[2],
                    idle = times[3] - pt[3],
                    iowait = times[4] - pt[4],
                    irq = times[5] - pt[5],
                    softirq = times[6] - pt[6],
                    totalDelta = totalDelta
                )
            }.sortedBy { it.coreIndex }
        } catch (_: Exception) { }
    }

    private fun readCpuStats(): Map<Int, LongArray> {
        return try {
            readCpuStatsFromFile(File("/proc/stat"))
        } catch (_: Exception) {
            try {
                val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "cat /proc/stat"))
                val text = process.inputStream.bufferedReader().readText()
                process.waitFor()
                readCpuStatsFromText(text)
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to read CPU stats via root", e2)
                emptyMap()
            }
        }
    }

    private fun readCpuStatsFromFile(file: File): Map<Int, LongArray> {
        val result = mutableMapOf<Int, LongArray>()
        file.bufferedReader().use { reader ->
            reader.lineSequence().forEach { line ->
                parseCpuLine(line, result)
            }
        }
        return result
    }

    private fun readCpuStatsFromText(text: String): Map<Int, LongArray> {
        val result = mutableMapOf<Int, LongArray>()
        text.lineSequence().forEach { line ->
            parseCpuLine(line, result)
        }
        return result
    }

    private fun parseCpuLine(line: String, result: MutableMap<Int, LongArray>) {
        if (!line.startsWith("cpu")) return
        val parts = line.trim().split("\\s+".toRegex())
        if (parts.size < 8) return
        val coreName = parts[0]
        if (coreName == "cpu") return
        val coreIndex = coreName.removePrefix("cpu").toIntOrNull() ?: return
        val times = LongArray(7)
        for (i in 1..7) {
            times[i - 1] = parts.getOrNull(i)?.toLongOrNull() ?: 0
        }
        result[coreIndex] = times
    }

    private data class RawUsage(
        val usagePercent: Float,
        val user: Long,
        val nice: Long,
        val system: Long,
        val idle: Long,
        val iowait: Long,
        val irq: Long,
        val softirq: Long,
        val totalDelta: Long
    )
}

data class CpuCoreUsage(
    val coreIndex: Int,
    val usagePercent: Float,
    val user: Long = 0,
    val nice: Long = 0,
    val system: Long = 0,
    val idle: Long = 0,
    val iowait: Long = 0,
    val irq: Long = 0,
    val softirq: Long = 0,
    val totalDelta: Long = 0
)
