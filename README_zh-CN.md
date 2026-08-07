# AutoTotem

[![License: MIT-NC-ND-Source](https://img.shields.io/badge/License-MIT--NC--ND--Source-yellow)](LICENSE)

**🌐 语言 / Language:** [English](README.md) · [简体中文](README_zh-CN.md)

一个基于 **Paper** 的 Minecraft 服务器插件，可以在玩家受到致命伤害时自动消耗背包中的不死图腾，避免死亡。

## ✨ 功能特性

- **自动消耗图腾**：玩家受到致命伤害时，自动从背包中消耗不死图腾并取消伤害
- **原版图腾效果**：触发后模拟原版不死图腾——恢复 1 点生命、清除火焰与药水效果，并施加生命恢复 II、伤害吸收 IV、抗火 I，同时播放原版音效与粒子特效
- **图腾查找顺序**：副手 → 主手 → 背包全部物品栏
- **独立玩家设置**：每个玩家的开关与冷却时间独立记忆
- **触发冷却**：可设置冷却时间，冷却期间不重复触发
- **致命伤害判定**：仅在伤害足以致死时触发，虚空掉落不会触发，计算包含伤害吸收值
- **多语言**：内置简体中文（zh-CN）与英文（en-US），按玩家客户端语言自动切换
- **Tab 补全**：命令完整支持 Tab 补全

## 📥 安装

1. 下载最新版本的 `AutoTotem-*.jar`
2. 将 jar 文件放入服务器的 `plugins/` 目录
3. 重启服务器（或使用插件管理器加载）
4. 首次启动后会在 `plugins/AutoTotem/` 生成配置文件

## 📋 命令

| 命令 | 说明 | 权限 |
| --- | --- | --- |
| `/autototem enabled true\|false` | 开启/关闭自动图腾（无参数时查询当前状态） | `autototem.use` |
| `/autototem cooldowntime [tick]` | 查看/设置触发冷却时间（20 tick = 1 秒） | `autototem.use` |
| `/autototem reload` | 重新加载配置文件 | `autototem.use` |
| `/autototem help` | 显示帮助信息 | `autototem.use` |

## 🔑 权限

| 权限 | 默认值 | 说明 |
| --- | --- | --- |
| `autototem.use` | `true` | 允许使用 `/autototem` 命令 |

## ⚙️ 配置

配置文件位于 `plugins/AutoTotem/config.yml`：

```yaml
# 玩家默认是否启用自动图腾
default-enabled: true
# 默认触发冷却时间（单位：tick，20 tick = 1 秒）
default-cooldown-ticks: 0
# 默认语言（当玩家客户端语言不在 lang/ 目录时使用，例如 en-US、zh-CN）
default-locale: en-US
```

## 🌍 本地化

AutoTotem 会在运行时加载 `lang/` 目录下的语言文件——只需放入一个新的 `.lang` 文件（例如 `de-DE.lang`）即可添加一种新语言，无需修改任何代码。

我们非常欢迎本地化贡献！如果你想添加或改进某种语言的翻译，欢迎提交 Pull Request，附上新增或更新的 `.lang` 文件即可。

## 🛠 构建

项目采用多模块构建，为每个受支持的 Minecraft 版本线产出独立的 jar：

| 模块 | 目标服务器 | 产物 |
| --- | --- | --- |
| `paper-1-20` | 1.20.1+ | `paper-1-20/build/libs/AutoTotem-1.20.jar` |
| `paper-1-21` | 1.21.x | `paper-1-21/build/libs/AutoTotem-1.21.jar` |
| `paper-26-1` | 26.1.x | `paper-26-1/build/libs/AutoTotem-26.1.jar` |
| `paper-26-2` | 26.2.x | `paper-26-2/build/libs/AutoTotem-26.2.jar` |

> 每个版本模块都针对各自的 Paper API 构建，并在 `plugin.yml` 中声明对应的 `api-version`，以正确处理新版本差异（如 1.20.5 起 `Particle.TOTEM` 更名为 `TOTEM_OF_UNDYING`）。

各版本模块所需的 JDK：

- `paper-1-20`：**JDK 17**
- `paper-1-21`：**JDK 21**
- `paper-26-1` / `paper-26-2`：**JDK 25**

安装所需 JDK 后，在 `gradle.properties` 中通过 `org.gradle.java.installations.paths` 指定其路径（分号分隔，使用正斜杠）。

```bash
# 构建全部版本的 jar
./gradlew build

# 或只构建单个版本（例如 1.21）
./gradlew :paper-1-21:build

# 构建产物位于 <模块>/build/libs/
# 本地启动 Paper 服务器进行测试（例如 1.21）
./gradlew :paper-1-21:runServer
```

## 📄 许可证

本项目使用 [带非商用与禁止闭源再分发条款的 MIT 许可证](LICENSE)——并非标准 MIT 协议。使用、修改或再分发前，请完整阅读 [LICENSE](LICENSE) 文件中的条款。

- **非商用**：商用须事先获得版权所有者的书面许可
- **仅开源分发**：任何再分发或衍生作品必须以源码形式、并在相同许可证下发布
