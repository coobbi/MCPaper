# MCPaper File Manager (Android)

一个使用 **Java + XML** 构建的独立 Android 文件管理器，面向 PaperMC 服务端目录管理场景。

## 功能
- 首页展示 `home/mcpaper` 根目录（应用私有外部目录中的 `home/mcpaper`）
- 点击文件夹进入子目录
- 点击文件打开文本编辑器并保存
- 右上角 `+` 与右下角悬浮按钮都可从手机存储导入文件到当前目录
- 适合手动导入 Minecraft 地图、插件等文件

## 目录示例
应用会在如下位置创建根目录：

`/storage/emulated/0/Android/data/com.example.mcpaper/files/home/mcpaper`

## 运行方式
1. 使用 Android Studio 打开项目根目录。
2. 等待 Gradle 同步完成。
3. 连接设备或启动模拟器后运行 `app` 模块。

## 注意
- 当前内置编辑器主要面向文本文件（如 `.properties`, `.yml`, `.txt`）。
- 二进制文件（`.jar`, `.zip`）可以导入但不建议在编辑器中打开。
