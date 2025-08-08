# AI 写作助手

一个智能的 Android 写作辅助应用，聚焦“更高效把 AI 文本输入到任意输入框”。

## 📝 核心价值

- 一键直填：将生成内容直接插入聊天、浏览器、办公等应用的输入框
- 跨应用快捷：通过磁贴或悬浮球快速唤起，无需来回切换复制粘贴
- 减少步骤：显著减少复制/切换/粘贴等重复操作，专注内容本身

## 🚀 主要特性

### 首次启动引导
1. 欢迎页：高效输入理念与模式对比
2. 工作模式选择：根据使用习惯选择最适合的模式
3. API 配置：支持 OpenAI、DeepSeek、Gemini 等
4. 完成设置：引导权限申请与快捷入口

### 双模式工作流程

#### 模式1：磁贴 + 剪贴板
1. 下拉通知栏点击 AI 写作磁贴
2. 选择预设，输入主题
3. 生成文本并自动复制到剪贴板
4. 在目标应用中粘贴使用

#### 模式2：悬浮球 + 直接输入
1. 点击屏幕边缘的悬浮球
2. 选择预设，输入主题
3. 一键将文本直接插入当前应用输入框
4. 无需手动复制粘贴，一步到位

## 🛠️ 技术架构

### 开发框架
- 语言：Kotlin
- UI：Jetpack Compose
- 架构：MVVM + Repository
- 数据库：Room
- 网络：Retrofit + OkHttp
- 异步：Kotlin Coroutines + Flow

### 核心组件
- 引导：OnboardingActivity + 多页面引导
- 主界面：MainActivity + 三页面导航（仪表盘/预设/设置）
- 磁贴服务：WritingTileService + 快速设置集成
- 悬浮窗服务：FloatingWindowService + 系统级悬浮
- 无障碍服务：AppAccessibilityService + 文本直接输入
- AI 集成：多提供商适配（OpenAI/DeepSeek/Gemini）

### 数据存储
- 用户偏好：SharedPreferences + Gson
- 预设管理：Room + Flow
- 历史记录：Room + 分页
- API 配置：本地存储，支持多提供商

## 🔧 环境要求
- 最低版本：Android 7.0 (API 24)
- 目标版本：Android 14 (API 34)
- 必需权限：
  - 网络访问（API 调用）
  - 通知权限（磁贴功能）
  - 悬浮窗权限（悬浮球模式）
  - 无障碍服务（直接输入模式）

## 📦 项目结构
```
app/src/main/java/com/aiwriter/assistant/
├── AIWriterApplication.kt                 # 应用程序主类
├── data/                                  # 数据层
│   ├── api/                               # API服务
│   ├── database/                          # 数据库和DAO
│   ├── model/                             # 数据模型
│   ├── preferences/                       # 偏好设置
│   └── repository/                        # 仓库层
├── service/                               # 系统服务
│   ├── AppAccessibilityService.kt         # 无障碍服务
│   ├── FloatingWindowService.kt           # 悬浮窗服务
│   └── WritingTileService.kt              # 磁贴服务
├── ui/                                    # 用户界面
│   ├── floating/                          # 悬浮窗界面
│   ├── main/                              # 主界面
│   ├── onboarding/                        # 引导界面
│   └── theme/                             # 主题样式
└── utils/                                 # 工具类
    └── PermissionHelper.kt                # 权限管理
```

## 🎨 设计亮点
- 弹性动画与触觉反馈，交互自然
- 悬浮球可拖拽至屏幕边缘任意位置
- 预设快速切换，便于重复场景
- 一键操作：复制/插入一步完成

## 🔒 隐私安全
- 本地处理：用户数据仅存储在本地设备
- API 密钥本地保存
- 权限最小化：仅申请必要权限
- 数据清除：支持一键清除

## 🛡️ 权限说明
- 通知权限：显示快速设置磁贴
- 悬浮窗权限：显示悬浮球和写作界面
- 无障碍服务：将生成的文本直接输入到其他应用（仅在用户主动插入时使用）

## 📄 开源协议

本项目采用 MIT 协议开源，详情请参阅 [LICENSE](LICENSE) 文件。

---

**AI 写作助手** - 让输入更高效，让创意更专注！ ✨
