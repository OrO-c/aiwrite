# GitHub Actions 构建指南

本项目配置了完整的 GitHub Actions CI/CD 流水线，支持自动和手动构建发布多架构 APK。

## 🚀 Workflow 概览

### 1. Build and Release APK (`build-release.yml`)
**主要发布流水线** - 构建正式版本并发布到 GitHub Releases

**触发条件：**
- 🏷️ **自动触发**: 推送版本标签（如 `v1.0.0`）
- 🖱️ **手动触发**: 通过 GitHub Actions 页面手动运行

**功能特性：**
- ✅ 多架构构建（ARM64、ARM32、x86_64、x86、Universal）
- ✅ 自动 APK 签名
- ✅ 版本管理
- ✅ GitHub Release 创建
- ✅ 构建报告生成

### 2. Build Debug APK (`build-debug.yml`)
**开发测试流水线** - 用于日常开发和 PR 检查

**触发条件：**
- 🔄 **自动触发**: 推送到 `main`/`develop` 分支
- 🔀 **自动触发**: 创建 Pull Request
- 🖱️ **手动触发**: 通过 GitHub Actions 页面

**功能特性：**
- ✅ Debug APK 构建
- ✅ 单元测试运行
- ✅ Lint 检查
- ✅ 代码质量分析

### 3. Security Scan (`security-scan.yml`)
**安全扫描流水线** - 定期安全检查和依赖扫描

**触发条件：**
- ⏰ **定期触发**: 每周一凌晨 2 点
- 🖱️ **手动触发**: 通过 GitHub Actions 页面
- 📝 **自动触发**: 修改依赖文件时

**功能特性：**
- ✅ 依赖漏洞扫描
- ✅ CodeQL 安全分析
- ✅ APK 安全扫描
- ✅ 权限审查

## 🎯 如何使用

### 方式一：自动发版（推荐）

1. **准备发版**
   ```bash
   # 1. 确保代码已提交到 main 分支
   git checkout main
   git pull origin main
   
   # 2. 创建版本标签
   git tag v1.0.0
   git push origin v1.0.0
   ```

2. **自动构建**
   - GitHub Actions 会自动检测到标签推送
   - 开始多架构构建流程
   - 自动创建 GitHub Release

3. **验证发版**
   - 访问 GitHub Releases 页面
   - 下载并测试各架构 APK

### 方式二：手动发版

1. **访问 Actions 页面**
   - 进入 GitHub 仓库的 Actions 标签页
   - 选择 "Build and Release APK" workflow

2. **配置构建参数**
   - 点击 "Run workflow" 
   - 填写版本信息：
     - **Version name**: `1.0.0`
     - **Version code**: `1`
     - **Build type**: `release`
     - **Create release**: ✅

3. **监控构建过程**
   - 实时查看构建日志
   - 等待所有架构构建完成

### 方式三：开发测试

1. **自动触发**
   ```bash
   # 推送到开发分支自动触发
   git push origin develop
   ```

2. **手动触发**
   - 访问 Actions → "Build Debug APK"
   - 点击 "Run workflow"

## 📱 多架构说明

### 支持的架构

| 架构 | 说明 | 适用设备 |
|------|------|----------|
| **Universal** | 通用包，支持所有架构 | 所有 Android 设备 |
| **ARM64 (arm64-v8a)** | 64位 ARM 架构 | 大多数现代手机 |
| **ARM32 (armeabi-v7a)** | 32位 ARM 架构 | 较老的 Android 设备 |
| **x86_64** | 64位 x86 架构 | 部分平板、模拟器 |
| **x86** | 32位 x86 架构 | 老旧模拟器、特殊设备 |

### 版本代码策略

为不同架构自动分配不同的版本代码：

```
基础版本代码: 1000
ARM32: 1000 + 1 = 1001
ARM64: 1000 + 2 = 1002  
x86:   1000 + 3 = 1003
x86_64: 1000 + 4 = 1004
Universal: 1000 + 0 = 1000
```

## 🔐 签名配置

### 必需的 GitHub Secrets

在仓库设置中配置以下 Secrets：

| Secret 名称 | 说明 | 示例 |
|-------------|------|------|
| `KEYSTORE_PASSWORD` | 密钥库密码 | `myStrongPassword123` |
| `KEY_ALIAS` | 密钥别名 | `ai-writer-key` |
| `KEY_PASSWORD` | 密钥密码 | `myKeyPassword123` |
| `KEYSTORE_FILE` | 密钥库文件（Base64） | `MIIEvgIBADANBgkqhkiG9w0B...` |

### 签名配置步骤

详细步骤请参考：[APK 签名配置指南](./SIGNING_SETUP.md)

## 📊 构建输出

### Artifacts（构建产物）

每次构建会生成以下 artifacts：

**Release 构建：**
- `apk-universal-release` - 通用 APK
- `apk-arm64-v8a-release` - ARM64 APK
- `apk-armeabi-v7a-release` - ARM32 APK
- `apk-x86_64-release` - x86_64 APK
- `apk-x86-release` - x86 APK

**Debug 构建：**
- `debug-apk` - Debug APK
- `test-results` - 测试报告
- `code-quality-reports` - 代码质量报告

**安全扫描：**
- `security-scan-results` - 安全扫描报告
- `dependency-reports` - 依赖分析报告

### GitHub Release

自动创建的 Release 包含：

- 📱 所有架构的 APK 文件
- 📋 详细的发版说明
- 🔍 文件校验和（checksums.txt）
- 📖 架构选择指南

## 🔧 故障排除

### 常见问题

1. **构建失败 - 签名错误**
   ```
   错误: Keystore was tampered with, or password was incorrect
   ```
   **解决方案:**
   - 检查 GitHub Secrets 中的密码是否正确
   - 确认 Base64 编码的密钥库文件是否完整

2. **构建失败 - 内存不足**
   ```
   错误: OutOfMemoryError
   ```
   **解决方案:**
   - 已在 workflow 中配置了足够的内存
   - 如果仍有问题，可以减少并行构建的架构数量

3. **版本冲突**
   ```
   错误: Version code already exists
   ```
   **解决方案:**
   - 检查版本代码是否与之前发布的版本冲突
   - 确保每次发布使用不同的版本号

### 调试技巧

1. **查看详细日志**
   - 点击失败的 workflow
   - 展开具体的步骤查看错误信息

2. **本地复现问题**
   ```bash
   # 本地运行相同的构建命令
   ./gradlew assembleRelease --stacktrace
   ```

3. **检查环境变量**
   - 确认所有必需的 Secrets 都已设置
   - 验证权限配置是否正确

## 🚀 最佳实践

### 版本管理

1. **语义化版本控制**
   ```
   v1.0.0 - 主要版本
   v1.1.0 - 次要版本  
   v1.0.1 - 修补版本
   ```

2. **分支策略**
   ```
   main     - 生产版本
   develop  - 开发版本
   feature/ - 功能分支
   release/ - 发布分支
   ```

### 构建优化

1. **缓存利用**
   - Gradle 依赖缓存
   - Android SDK 缓存
   - 构建缓存

2. **并行构建**
   - 多架构并行构建
   - 独立的构建任务

3. **资源优化**
   - 压缩资源文件
   - 代码混淆
   - 无用代码移除

### 安全考虑

1. **敏感信息保护**
   - 使用 GitHub Secrets
   - 不在代码中硬编码密码
   - 定期轮换密钥

2. **权限最小化**
   - 只授予必要的权限
   - 定期审查权限配置

3. **安全扫描**
   - 定期运行安全扫描
   - 及时更新依赖
   - 监控漏洞报告

## 📞 获取帮助

如果遇到问题：

1. 📚 查看 [签名配置指南](./SIGNING_SETUP.md)
2. 🔍 检查 GitHub Actions 日志
3. 📖 参考 Android 官方文档
4. 🐛 提交 Issue 描述问题

---

通过这套完整的 CI/CD 流水线，您可以轻松地构建、测试和发布高质量的 Android 应用！