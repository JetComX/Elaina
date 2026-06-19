package com.jetcomx.elaina.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.blur.RuntimeShader
import top.yukonga.miuix.kmp.blur.asBrush
import top.yukonga.miuix.kmp.blur.isRuntimeShaderSupported
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.floor

enum class DeviceType { PHONE, PAD }

private const val OS3_BG_FRAG = """
uniform vec2 uResolution;uniform float uAnimTime;uniform vec4 uBound;
uniform float uTranslateY;uniform vec3 uPoints[4];uniform vec2 uPointsAnim[4];
uniform vec4 uColors[4];uniform float uAlphaMulti;uniform float uNoiseScale;
uniform float uPointRadiusMulti;uniform float uSaturateOffset;uniform float uLightOffset;
vec3 rgb2hsv(vec3 c){vec4 K=vec4(0.0,-1.0/3.0,2.0/3.0,-1.0);
vec4 p=mix(vec4(c.bg,K.wz),vec4(c.gb,K.xy),step(c.b,c.g));
vec4 q=mix(vec4(p.xyw,c.r),vec4(c.r,p.yzx),step(p.x,c.r));
float d=q.x-min(q.w,q.y);float e=1.0e-10;
return vec3(abs(q.z+(q.w-q.y)/(6.0*d+e)),d/(q.x+e),q.x);}
vec3 hsv2rgb(vec3 c){vec4 K=vec4(1.0,2.0/3.0,1.0/3.0,3.0);
vec3 p=abs(fract(c.xxx+K.xyz)*6.0-K.www);
return c.z*mix(K.xxx,clamp(p-K.xxx,0.0,1.0),c.y);}
float hash(vec2 p){vec3 p3=fract(vec3(p.xyx)*0.13);p3+=dot(p3,p3.yzx+3.333);
return fract((p3.x+p3.y)*p3.z);}
float perlin(vec2 x){vec2 i=floor(x);vec2 f=fract(x);
float a=hash(i);float b=hash(i+vec2(1.0,0.0));
float c=hash(i+vec2(0.0,1.0));float d=hash(i+vec2(1.0,1.0));
vec2 u=f*f*(3.0-2.0*f);
return mix(a,b,u.x)+(c-a)*u.y*(1.0-u.x)+(d-b)*u.x*u.y;}
float gradientNoise(in vec2 uv){return fract(52.9829189*fract(dot(uv,vec2(0.06711056,0.00583715))));}
vec4 main(vec2 fc){vec2 vUv=fc/uResolution;vUv.y=1.0-vUv.y;vec2 uv=vUv;
uv-=vec2(0.,uTranslateY);uv.xy-=uBound.xy;uv.xy/=uBound.zw;
vec4 col=vec4(0.0);float nv=perlin(vUv*uNoiseScale+vec2(-uAnimTime,-uAnimTime));
for(int i=0;i<4;i++){vec4 pc=uColors[i];pc.rgb*=pc.a;
vec2 pt=uPointsAnim[i];float rad=uPoints[i].z*uPointRadiusMulti;
float d=distance(uv,pt);float pct=smoothstep(rad,0.,d);
col.rgb=mix(col.rgb,pc.rgb,pct);col.a=mix(col.a,pc.a,pct);}
float onv=smoothstep(0.,1.,nv);col.rgb/=col.a;
vec3 hsv=rgb2hsv(col.rgb);hsv.y=mix(hsv.y,0.0,onv*uSaturateOffset);
col.rgb=hsv2rgb(hsv);col.rgb+=onv*uLightOffset;
col.a=clamp(col.a,0.,1.);col.a*=uAlphaMulti;
col+=(10.0/255.0)*gradientNoise(fc.xy)-(5.0/255.0);
return vec4(col.rgb*col.a,col.a);}
"""

private data class BgEffectConfig(
    val points: FloatArray,
    val colors1: FloatArray, val colors2: FloatArray, val colors3: FloatArray,
    val colorInterpPeriod: Float,
    val lightOffset: Float, val saturateOffset: Float, val pointOffset: Float,
)

private val PHONE_LIGHT = BgEffectConfig(
    points = floatArrayOf(0.8f,0.2f,1.0f,0.8f,0.9f,1.0f,0.2f,0.9f,1.0f,0.2f,0.2f,1.0f),
    colors1 = floatArrayOf(1f,0.9f,0.94f,1f,1f,0.84f,0.89f,1f,0.97f,0.73f,0.82f,1f,0.64f,0.65f,0.98f,1f),
    colors2 = floatArrayOf(0.58f,0.74f,1f,1f,1f,0.9f,0.93f,1f,0.74f,0.76f,1f,1f,0.97f,0.77f,0.84f,1f),
    colors3 = floatArrayOf(0.98f,0.86f,0.9f,1f,0.6f,0.73f,0.98f,1f,0.92f,0.93f,1f,1f,0.56f,0.69f,1f,1f),
    colorInterpPeriod = 5f, lightOffset = 0.1f, saturateOffset = 0.2f, pointOffset = 0.2f,
)

private val PHONE_DARK = BgEffectConfig(
    points = floatArrayOf(0.8f,0.2f,1.0f,0.8f,0.9f,1.0f,0.2f,0.9f,1.0f,0.2f,0.2f,1.0f),
    colors1 = floatArrayOf(0.2f,0.06f,0.88f,0.4f,0.3f,0.14f,0.55f,0.5f,0f,0.64f,0.96f,0.5f,0.11f,0.16f,0.83f,0.4f),
    colors2 = floatArrayOf(0.07f,0.15f,0.79f,0.5f,0.62f,0.21f,0.67f,0.5f,0.06f,0.25f,0.84f,0.5f,0f,0.2f,0.78f,0.5f),
    colors3 = floatArrayOf(0.58f,0.3f,0.74f,0.4f,0.27f,0.18f,0.6f,0.5f,0.66f,0.26f,0.62f,0.5f,0.12f,0.16f,0.7f,0.6f),
    colorInterpPeriod = 8f, lightOffset = 0f, saturateOffset = 0.17f, pointOffset = 0.4f,
)

@Composable
fun BgEffectBackground(
    dynamicBackground: Boolean = false,
    modifier: Modifier = Modifier,
    bgModifier: Modifier = Modifier,
    isFullSize: Boolean = false,
    effectBackground: Boolean = true,
    alpha: () -> Float = { 1f },
    content: @Composable BoxScope.() -> Unit,
) {
    if (!isRuntimeShaderSupported()) {
        Box(modifier = modifier, content = content)
        return
    }
    Box(modifier = modifier) {
        val surface = MiuixTheme.colorScheme.surface
        val isDarkTheme = isSystemInDarkTheme()
        val painter = remember { BgEffectPainter() }
        val preset = remember(isDarkTheme) { if (isDarkTheme) PHONE_DARK else PHONE_LIGHT }
        val colorStage = remember { Animatable(0f) }

        LaunchedEffect(dynamicBackground, preset) {
            if (!dynamicBackground) return@LaunchedEffect
            val animatesColors = !preset.colors1.contentEquals(preset.colors2) || !preset.colors2.contentEquals(preset.colors3)
            if (!animatesColors) return@LaunchedEffect
            var targetStage = floor(colorStage.value) + 1f
            while (isActive) {
                kotlinx.coroutines.delay((preset.colorInterpPeriod * 500).toLong())
                colorStage.animateTo(targetStage, spring(dampingRatio = 0.9f, stiffness = 35f))
                targetStage += 1f
            }
        }

        Spacer(
            modifier = Modifier.fillMaxSize().then(bgModifier).bgEffectDraw(
                painter = painter, preset = preset, isDarkTheme = isDarkTheme,
                surface = surface, effectBackground = effectBackground,
                isFullSize = isFullSize, playing = dynamicBackground,
                colorStage = { colorStage.value }, alpha = alpha,
            )
        )
        content()
    }
}

private class BgEffectPainter {
    val runtimeShader by lazy {
        RuntimeShader(OS3_BG_FRAG).also {
            it.setFloatUniform("uTranslateY", 0f)
            it.setFloatUniform("uNoiseScale", 1.5f)
            it.setFloatUniform("uPointRadiusMulti", 1f)
            it.setFloatUniform("uAlphaMulti", 1f)
        }
    }

    private val resolution = FloatArray(2)
    private val bound = FloatArray(4)
    private val colorsBuffer = FloatArray(16)
    private val pointsAnimBuffer = FloatArray(8)
    private var animTime = Float.NaN
    private var cachedColorStage = Float.NaN
    private var cachedColorsPreset: BgEffectConfig? = null
    private var cachedPointsAnimTime = Float.NaN
    private var cachedPointsAnimPreset: BgEffectConfig? = null

    fun updateResolution(w: Float, h: Float) {
        if (resolution[0] == w && resolution[1] == h) return
        resolution[0] = w; resolution[1] = h
        runtimeShader.setFloatUniform("uResolution", resolution)
    }

    fun updateBound(logoH: Float, totalH: Float, totalW: Float) {
        val hr = logoH / totalH
        if (totalW <= totalH) { bound[0]=0f; bound[1]=1f-hr; bound[2]=1f; bound[3]=hr }
        else { val ar=totalW/totalH; val cy=1f-hr/2f; bound[0]=0f; bound[1]=cy-ar/2f; bound[2]=1f; bound[3]=ar }
        runtimeShader.setFloatUniform("uBound", bound)
    }

    fun updateAnimTime(t: Float) {
        if (animTime == t) return; animTime = t
        runtimeShader.setFloatUniform("uAnimTime", animTime)
    }

    fun updatePointsAnim(t: Float, preset: BgEffectConfig) {
        if (cachedPointsAnimTime == t && cachedPointsAnimPreset === preset) return
        val off = preset.pointOffset
        for (i in 0 until 4) {
            val sx = preset.points[i*3]; val sy = preset.points[i*3+1]
            pointsAnimBuffer[i*2] = sx + kotlin.math.sin(t+sy) * off
            pointsAnimBuffer[i*2+1] = sy + kotlin.math.cos(t+sx) * off
        }
        runtimeShader.setFloatUniform("uPointsAnim", pointsAnimBuffer)
        cachedPointsAnimTime = t; cachedPointsAnimPreset = preset
    }

    fun updateColors(preset: BgEffectConfig, stage: Float) {
        if (cachedColorsPreset === preset && cachedColorStage == stage) return
        val base = stage.toInt(); val frac = stage - base
        val s = cci(preset, base); val e = cci(preset, base+1)
        for (i in 0 until 16) colorsBuffer[i] = s[i] + (e[i]-s[i]) * frac
        runtimeShader.setFloatUniform("uColors", colorsBuffer)
        cachedColorsPreset = preset; cachedColorStage = stage
    }

    private fun cci(p: BgEffectConfig, i: Int) = when(i.mod(4)) { 1->p.colors3; 3->p.colors1; else->p.colors2 }

    fun applyPreset(p: BgEffectConfig) {
        runtimeShader.setFloatUniform("uPoints", p.points)
        runtimeShader.setFloatUniform("uLightOffset", p.lightOffset)
        runtimeShader.setFloatUniform("uSaturateOffset", p.saturateOffset)
    }
}

private fun Modifier.bgEffectDraw(
    painter: BgEffectPainter, preset: BgEffectConfig, isDarkTheme: Boolean,
    surface: Color, effectBackground: Boolean, isFullSize: Boolean,
    playing: Boolean, colorStage: () -> Float, alpha: () -> Float,
): Modifier = this then BgEffectElement(painter, preset, isDarkTheme, surface, effectBackground, isFullSize, playing, colorStage, alpha)

private data class BgEffectElement(
    val painter: BgEffectPainter, val preset: BgEffectConfig, val isDarkTheme: Boolean,
    val surface: Color, val effectBackground: Boolean, val isFullSize: Boolean,
    val playing: Boolean, val colorStage: () -> Float, val alpha: () -> Float,
) : ModifierNodeElement<BgEffectNode>() {
    override fun create() = BgEffectNode(painter, preset, isDarkTheme, surface, effectBackground, isFullSize, playing, colorStage, alpha)
    override fun update(node: BgEffectNode) { node.update(painter, preset, isDarkTheme, surface, effectBackground, isFullSize, playing, colorStage, alpha) }
}

private class BgEffectNode(
    private var painter: BgEffectPainter, private var preset: BgEffectConfig,
    private var isDarkTheme: Boolean, private var surface: Color,
    private var effectBackground: Boolean, private var isFullSize: Boolean,
    private var playing: Boolean, private var colorStage: () -> Float, private var alpha: () -> Float,
) : Modifier.Node(), DrawModifierNode {
    private var animJob: Job? = null
    private var animTime = 0f
    private var startOff = 0f

    override fun onAttach() { if (playing) startAnim() }
    override fun onDetach() { animJob?.cancel(); animJob = null }

    fun update(
        painter: BgEffectPainter, preset: BgEffectConfig, isDarkTheme: Boolean,
        surface: Color, effectBackground: Boolean, isFullSize: Boolean,
        playing: Boolean, colorStage: () -> Float, alpha: () -> Float,
    ) {
        this.painter = painter; this.preset = preset; this.isDarkTheme = isDarkTheme
        this.surface = surface; this.effectBackground = effectBackground
        this.isFullSize = isFullSize; this.colorStage = colorStage; this.alpha = alpha
        if (this.playing != playing) { this.playing = playing; if (playing) startAnim() else { animJob?.cancel(); animJob = null } }
        invalidateDraw()
    }

    private fun startAnim() {
        animJob?.cancel(); startOff = animTime
        animJob = coroutineScope.launch {
            val minD = 1_000_000_000L / 60L; val origin = withFrameNanos { it }; var last = origin
            while (isActive) { val now = withFrameNanos { it }; if (now - last < minD) continue; last = now; animTime = startOff + (now-origin)/1_000_000_000f; invalidateDraw() }
        }
    }

    override fun ContentDrawScope.draw() {
        drawRect(surface)
        if (effectBackground) {
            val av = alpha()
            if (av > 0f) {
                val dh = if (isFullSize) size.height * 0.8f else size.height * 0.5f
                painter.updateResolution(size.width, size.height)
                painter.updateBound(dh, size.height, size.width)
                painter.applyPreset(preset)
                painter.updateColors(preset, colorStage())
                painter.updateAnimTime(animTime)
                painter.updatePointsAnim(animTime, preset)
                drawRect(painter.runtimeShader.asBrush(), alpha = av)
            }
        }
        drawContent()
    }
}
