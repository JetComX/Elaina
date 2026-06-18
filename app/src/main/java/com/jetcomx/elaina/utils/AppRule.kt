package com.jetcomx.elaina.utils

import android.graphics.Bitmap

data class AppRule(
    val packageName: String,
    val processName: String? = null,
    val threadName: String?,
    val cpuAffinity: String
)

data class AppRuleGroup(
    val packageName: String,
    val appLabel: String?,
    val appIcon: Bitmap? = null,
    val rules: List<AppRule>
)

data class ThreadCpuInfo(
    val tid: Int,
    val name: String,
    val cpuPercent: Float
)

object AppListParser {

    fun parse(configContent: String): List<AppRule> {
        val rules = mutableListOf<AppRule>()
        configContent.lineSequence().forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#")) return@forEach
            val rule = parseLine(trimmed) ?: return@forEach
            rules.add(rule)
        }
        return rules
    }

    fun groupByPackage(rules: List<AppRule>): List<AppRuleGroup> {
        val map = linkedMapOf<String, MutableList<AppRule>>()
        for (rule in rules) {
            map.getOrPut(rule.packageName) { mutableListOf() }.add(rule)
        }
        return map.map { (pkg, list) ->
            AppRuleGroup(
                packageName = pkg,
                appLabel = null,
                rules = list
            )
        }
    }

    fun parseLine(line: String): AppRule? {
        val eqIndex = line.indexOf('=')
        if (eqIndex <= 0) return null

        val left = line.substring(0, eqIndex).trim()
        val cpuAffinity = line.substring(eqIndex + 1).trim()
        if (cpuAffinity.isEmpty()) return null

        val braceStart = left.indexOf('{')
        val braceEnd = left.indexOf('}')
        val threadName: String?
        val remaining: String
        if (braceStart >= 0 && braceEnd > braceStart) {
            threadName = left.substring(braceStart + 1, braceEnd)
            remaining = left.substring(0, braceStart)
        } else {
            threadName = null
            remaining = left
        }

        val colonIndex = remaining.lastIndexOf(':')
        val packageName: String
        val processName: String?
        if (colonIndex >= 0) {
            packageName = remaining.substring(0, colonIndex)
            processName = remaining.substring(colonIndex)
        } else {
            packageName = remaining
            processName = null
        }

        if (packageName.isEmpty()) return null
        return AppRule(
            packageName = packageName,
            processName = processName,
            threadName = threadName,
            cpuAffinity = cpuAffinity
        )
    }

    fun AppRule.toConfigLine(): String {
        val processPart = processName.orEmpty()
        val threadPart = if (threadName != null) "{$threadName}" else ""
        return "$packageName$processPart$threadPart=$cpuAffinity"
    }

    fun serialize(commentHeader: String, rules: List<AppRule>): String {
        return buildString {
            if (commentHeader.isNotBlank()) {
                appendLine(commentHeader)
            }
            rules.forEach { rule ->
                appendLine(rule.toConfigLine())
            }
        }
    }
}
