# Elaina

Elaina 是一款 Android 应用，通过将应用进程绑定到指定 CPU 核心来优化设备性能和功耗，实现精细化 CPU 亲和性管理。需要 Root 权限和 AppOpt Magisk 模块。

## 名称来源

Elaina 取自动漫 **[魔女之旅]** 中主人公 **伊蕾娜（イレイナ / Elaina）** 的英文名。伊蕾娜是一位旅行魔女，自由、优雅而充满好奇心 — 这也正是本项目所追求的气质。

## 特性

### 核心功能
- **CPU 核心绑定**：将应用/进程/线程绑定到指定 CPU 核心组（省电 / 均衡 / 高性能）
- **实时 CPU 监控**：折线图 / 柱状图实时显示各核心使用率
- **系统信息展示**：设备型号、CPU 核心数/线程数、制造商
- **Root 管理器检测**：自动识别 Magisk / KernelSU / APatch 并获取版本号，支持点击跳转
- **规则管理**：支持添加、编辑、删除 CPU 亲和性规则

### 双 UI 样式
- **MiuiX**：标准 Miuix Design 风格（Material You）
- **LiquidGlass**：液态玻璃风格，基于 Kyant Backdrop 实现实时模糊、透镜折射、活力色彩效果

### LiquidGlass 自定义（设置 → UI 样式 → LiquidGlass → 自定义 LiquidGlass 视觉效果）
- **Vibrancy（活力效果）**：背景色彩渗透
- **Blur Radius（模糊半径）**：毛玻璃模糊强度 (0–50 dp)
- **Lens Dilation（透镜扩张）**：透镜折射区域
- **Lens Blur（透镜模糊）**：透镜边缘扩散
- **Chromatic Aberration（色散）**：彩虹折射效果
- **Highlight（高光）**：按压时环境光反射，增强玻璃质感
- **恢复默认值**：一键重置所有参数

### 外观设置
- **主题系统**：跟随系统 / 浅色 / 深色 / Monet 取色
- **自定义强调色**：Monet 模式下自定义种子色（带展开/收起动画）
- **背景样式**：默认 / 梦幻流体渐变 / 自定义图片
- **卡片按压效果**：无反馈 / 下沉 / 倾斜

### 其他功能
- **更新检查**：启动时自动检查 Gitee Release 新版本
- **调试模式**：完整日志输出
- **崩溃记录**：自动记录崩溃页面和用户操作
- **日志导出**：保存或分享应用日志

## 技术架构

```
Elaina
├── navigation/          # 导航 (NavBar, MainNavScreen)
├── screen/
│   ├── home/           # 主页
│   ├── thread/         # 线程管理
│   ├── log/            # 日志查看
│   ├── settings/       # 设置
│   ├── loading/        # 启动加载
│   └── welcome/        # 欢迎页
├── ui/
│   ├── component/      # LiquidGlass 组件库 (20+ 组件)
│   └── utils/          # UI 工具 (InteractiveHighlight, DampedDragAnimation)
├── utils/              # 业务工具
│   ├── AppSettings.kt / SettingsStore.kt  # 设置管理 (DataStore)
│   ├── CrashHandler.kt                    # 崩溃捕获
│   ├── UpdateChecker.kt                   # 更新检查 (GitHub API)
│   ├── RootUtils.kt                       # Root 检测 (Magisk/KSU/APatch)
│   ├── ModuleChecker.kt                   # 模块检测
│   ├── CpuMonitor.kt                      # CPU 监控
│   └── SystemInfoProvider.kt              # 系统信息
├── service/            # 后台服务
└── viewmodel/          # ViewModel
```

## 依赖项

- **Jetpack Compose** + **Material3**：声明式 UI
- **Miuix KMP**：MIUI/HyperOS 风格组件库
- **Kyant Backdrop**：液态玻璃引擎（vibrancy, blur, lens）
- **Kyant Shapes**：Capsule / RoundedRectangle 形状
- **Vico**：CPU 图表（折线图 / 柱状图）
- **Coil**：图片加载
- **DataStore**：键值持久化
- **Kotlin Coroutines + Flow**：异步与响应式数据流
- **AndroidX Navigation** + **Navigation3**：导航框架
- **Material Icons Extended**：图标库

## 构建

```bash
git clone https://github.com/JetComX/Elaina.git
cd thread-box
./gradlew assembleDebug
```

### 环境要求
- Android Studio Hedgehog (2023.1.1) 或更高
- Kotlin 2.2+
- AGP 9.2+
- compileSdk 37
- minSdk 31

## 使用说明

1. **安装**：从 Release 页面下载 APK
2. **Root 权限**：需要 Root 权限设置 CPU 亲和性
3. **Magisk 模块**：安装 AppOpt（线程优化模块），应用内可"获取模块"
4. **添加规则**：线程页 → 右下角 + → 填写包名、CPU 核心范围
5. **切换 UI**：设置 → UI 样式 → MiuiX / LiquidGlass
6. **自定义效果**：LiquidGlass 模式下 → 自定义 LiquidGlass 视觉效果

## 权限

| 权限 | 用途 |
|------|------|
| INTERNET | 检查更新 |
| ACCESS_NETWORK_STATE | 检测网络状态 |
| FOREGROUND_SERVICE | 后台服务 |
| QUERY_ALL_PACKAGES | 查询已安装应用 |

## 致谢

特别致谢 [线程优化 (AppOpt)](http://appopt.suto.top/) by SutoLiu — Elaina 核心依赖的 Magisk 模块。

感谢以下开源项目：
Jetpack Compose、Miuix KMP、Kyant Backdrop、Kyant Shapes、Kotlin Coroutines、Vico、Coil、AndroidX Navigation、DataStore、Material Icons Extended、AndroidX Lifecycle、NavigationEvent Compose

完整列表详见应用内 设置 → 致谢。

## 许可证

MIT License

## 作者

By [JetComX](https://github.com/JetComX)
