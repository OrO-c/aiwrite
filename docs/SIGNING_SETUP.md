# APK 签名配置指南

本文档详细说明如何为 AI 写作助手应用配置 APK 签名，以便在 GitHub Actions 中自动构建和发布 release 版本的 APK。

## 🔐 生成签名密钥

### 1. 创建密钥库

在本地开发环境中，使用以下命令生成密钥库：

```bash
keytool -genkey -v -keystore ai-writer-release.keystore \
  -alias ai-writer-key \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass your_keystore_password \
  -keypass your_key_password \
  -dname "CN=AI Writing Assistant, OU=Development, O=Your Company, L=Your City, S=Your State, C=CN"
```

**重要参数说明：**
- `your_keystore_password`: 密钥库密码（请使用强密码）
- `your_key_password`: 密钥密码（可以与密钥库密码相同）
- 修改 `dname` 中的信息为您的实际信息

### 2. 验证密钥库

```bash
keytool -list -v -keystore ai-writer-release.keystore -alias ai-writer-key
```

## 🛠️ 配置 GitHub Secrets

在 GitHub 仓库中配置以下 Secrets（Settings → Secrets and variables → Actions）：

### 必需的 Secrets

1. **KEYSTORE_PASSWORD**: 密钥库密码
2. **KEY_ALIAS**: 密钥别名（例如：`ai-writer-key`）
3. **KEY_PASSWORD**: 密钥密码
4. **KEYSTORE_FILE**: 密钥库文件的 Base64 编码

### 获取密钥库的 Base64 编码

```bash
# 在本地生成 Base64 编码
base64 -i ai-writer-release.keystore | pbcopy  # macOS
base64 -i ai-writer-release.keystore           # Linux

# 或者使用在线工具将 .keystore 文件转换为 Base64
```

## 📱 配置应用签名

### 1. 更新 app/build.gradle

在 `android` 块中添加签名配置：

```gradle
android {
    // ... 其他配置

    signingConfigs {
        release {
            if (project.hasProperty('KEYSTORE_FILE')) {
                storeFile file(KEYSTORE_FILE)
                storePassword KEYSTORE_PASSWORD
                keyAlias KEY_ALIAS
                keyPassword KEY_PASSWORD
            }
        }
    }

    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

### 2. 创建 gradle.properties（本地开发）

在项目根目录创建 `gradle.properties`（不要提交到 Git）：

```properties
# 签名配置（仅用于本地开发）
KEYSTORE_FILE=ai-writer-release.keystore
KEYSTORE_PASSWORD=your_keystore_password
KEY_ALIAS=ai-writer-key
KEY_PASSWORD=your_key_password
```

### 3. 更新 .gitignore

确保敏感文件不被提交：

```gitignore
# 签名文件
*.keystore
*.jks
gradle.properties
keystore.properties
```

## 🚀 GitHub Actions 自动签名

GitHub Actions workflow 会自动处理签名过程：

### 自动触发（推送 tag）

```bash
git tag v1.0.0
git push origin v1.0.0
```

### 手动触发

1. 访问 GitHub Actions 页面
2. 选择 "Build and Release APK" workflow
3. 点击 "Run workflow"
4. 输入版本信息并运行

## 🔍 验证签名

### 验证本地构建的 APK

```bash
# 查看 APK 签名信息
jarsigner -verify -verbose -certs app/build/outputs/apk/release/app-release.apk

# 查看证书详情
keytool -printcert -jarfile app/build/outputs/apk/release/app-release.apk
```

### 验证 GitHub Actions 构建的 APK

下载构建的 APK 后，使用相同命令验证签名。

## 📋 多架构构建说明

workflow 会自动生成以下版本的 APK：

- `AI-Writing-Assistant-v1.0.0-universal-release.apk` - 通用版本
- `AI-Writing-Assistant-v1.0.0-arm64-v8a-release.apk` - ARM64 版本
- `AI-Writing-Assistant-v1.0.0-armeabi-v7a-release.apk` - ARM32 版本
- `AI-Writing-Assistant-v1.0.0-x86_64-release.apk` - x86_64 版本
- `AI-Writing-Assistant-v1.0.0-x86-release.apk` - x86 版本

每个架构会有不同的版本代码，以便在 Google Play Store 中正确管理。

## 🔒 安全最佳实践

### 1. 密钥管理
- 使用强密码（至少 12 位，包含数字、字母、特殊字符）
- 定期轮换密码
- 备份密钥库文件到安全位置
- 不要在代码中硬编码密码

### 2. GitHub Secrets
- 只添加必要的 Secrets
- 定期审查 Secrets 的使用情况
- 限制仓库的访问权限

### 3. 发布验证
- 每次发布前验证 APK 签名
- 检查 APK 的权限和证书信息
- 在多种设备上测试安装

## 🛠️ 故障排除

### 常见问题

1. **密钥库密码错误**
   - 检查 GitHub Secrets 中的密码是否正确
   - 确认密钥别名是否匹配

2. **签名失败**
   - 验证密钥库文件的 Base64 编码是否正确
   - 检查密钥是否过期

3. **构建失败**
   - 查看 GitHub Actions 日志
   - 确认所有必需的 Secrets 都已配置

### 调试命令

```bash
# 本地测试签名
./gradlew assembleRelease

# 查看构建输出
ls -la app/build/outputs/apk/release/

# 验证签名
jarsigner -verify app/build/outputs/apk/release/*.apk
```

## 📞 获取帮助

如果遇到签名相关问题：

1. 检查 GitHub Actions 的构建日志
2. 验证本地密钥库配置
3. 确认所有 Secrets 都已正确设置
4. 参考 Android 官方文档

---

**重要提示**: 请妥善保管您的签名密钥，丢失密钥将无法更新已发布的应用！