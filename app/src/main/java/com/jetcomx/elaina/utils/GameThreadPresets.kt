package com.jetcomx.elaina.utils

object GameThreadPresets {
    val presets: Map<String, List<String>> = mapOf(
        
        "com.tencent.tmgp.sgame" to listOf(
            "GameThread", "RenderThread", "VulkanThread",
            "AudioThread", "PhysicsThread", "MainThread"
        ),
        "com.tencent.tmgp.pubgmhd" to listOf(
            "GameThread", "RenderThread", "RHIThread",
            "AudioThread", "PhysXThread", "MainThread"
        ),
        "com.tencent.jkchess" to listOf(
            "UnityMain", "UnityGfxDeviceW", "UnityPreload",
            "UnitySubstanceThread", "AudioThread"
        ),
        "com.tencent.tmgp.dfm" to listOf(
            "GameThread", "RenderThread", "RHIThread",
            "AudioThread", "PhysicsThread"
        ),
        "com.tencent.tmgp.cf" to listOf(
            "UnityMain", "UnityGfxDeviceW", "UnityPreload",
            "RenderThread", "AudioThread"
        ),
        "com.tencent.tmgp.aqtw" to listOf(
            "UnityMain", "UnityGfxDeviceW", "UnityPreload",
            "AudioThread", "TerrainThread"
        ),
        "com.tencent.tmgp.speedmobile" to listOf(
            "UnityMain", "UnityGfxDeviceW", "UnityPreload",
            "PhysicsThread", "RenderThread"
        ),
        "com.tencent.tmgp.hyrz" to listOf(
            "UnityMain", "UnityGfxDeviceW", "UnityPreload",
            "RenderThread"
        ),
        "com.tencent.tmgp.valorant" to listOf(
            "GameThread", "RenderThread", "RHIThread",
            "AudioThread", "MainThread"
        ),
        "com.tencent.tmgp.lol" to listOf(
            "UnityMain", "UnityGfxDeviceW", "UnityPreload",
            "RenderThread", "PhysicsThread"
        ),
        
        "com.miHoYo.Yuanshen" to listOf(
            "UnityMain", "UnityGfxDeviceW", "UnityPreload",
            "UnitySubstanceThread", "Job.Worker*"
        ),
        "com.miHoYo.hkrpg" to listOf(
            "UnityMain", "UnityGfxDeviceW", "UnityPreload",
            "Job.Worker*", "AudioThread"
        ),
        "com.miHoYo.Nap" to listOf(
            "UnityMain", "UnityGfxDeviceW", "UnityPreload",
            "Job.Worker*", "RenderThread"
        ),
        "com.miHoYo.bh3" to listOf(
            "UnityMain", "UnityGfxDeviceW", "UnityPreload",
            "Job.Worker*"
        ),
        "com.miHoYo.wd" to listOf(
            "UnityMain", "UnityGfxDeviceW", "UnityPreload"
        ),
        
        "com.netease.party" to listOf(
            "UnityMain", "UnityGfxDeviceW", "UnityPreload",
            "AudioThread", "PhysicsThread"
        ),
        "com.netease.sky" to listOf(
            "UnityMain", "UnityGfxDeviceW", "UnityPreload",
            "RenderThread"
        ),
        "com.netease.dwrg" to listOf(
            "UnityMain", "UnityGfxDeviceW", "UnityPreload",
            "RenderThread"
        ),
        "com.netease.onmyoji" to listOf(
            "UnityMain", "UnityGfxDeviceW", "UnityPreload",
            "RenderThread", "AudioThread"
        ),
        "com.netease.ni Shuihan" to listOf(
            "UnityMain", "UnityGfxDeviceW", "UnityPreload",
            "AudioThread", "TerrainThread"
        ),
        "com.tencent.tmgp.cod" to listOf(
            "GameThread", "RenderThread", "RHIThread",
            "AudioThread", "MainThread"
        )
    )
}
