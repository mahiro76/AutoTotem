# AutoTotem

[![License: MIT-NC-ND-Source](https://img.shields.io/badge/License-MIT--NC--ND--Source-yellow)](LICENSE)

**🌐 语言 / Language:** [English](README.md) · [简体中文](README_zh-CN.md)

一个基于 **Paper** 的 Minecraft 服务器插件，可以在玩家受到致命伤害时自动消耗背包中的不死图腾，避免死亡。单个 jar 即可适配 Minecraft **1.20.1 – 26.2**。

## ✨ 功能特性

- **自动消耗图腾**：玩家受到致命伤害时，自动从背包中消耗不死图腾并取消伤害
- **原版图腾效果**：触发后模拟原版不死图腾——恢复 1 点生命、清除火焰与药水效果，并施加生命恢复 II、伤害吸收 IV、抗火 I，同时播放原版音效与粒子特效
- **图腾查找顺序**：副手 → 主手 → 背包全部物品栏
- **独立玩家设置**：每个玩家的开关与冷却时间独立记忆
- **触发冷却**：可设置冷却时间，冷却期间不重复触发
- **致命伤害判定**：仅在伤害足以致死时触发，虚空掉落不会触发，计算包含伤害吸收值
- **PVP 触发开关**：可配置关闭由玩家攻击造成的致命伤害触发（`pvp-enabled` 配置项）
- **多语言**：内置简体中文（zh-CN）与英文（en-US），按玩家客户端语言自动切换
- **Tab 补全**：命令完整支持 Tab 补全，并按权限过滤管理员专属子命令

## 📥 安装

1. 从[modrinth](https://modrinth.com/plugin/autototem-plugin)下载最新版本的 `AutoTotem-*.jar`
2. 将 jar 文件放入服务器的 `plugins/` 目录
3. 重启服务器（或使用插件管理器加载）
4. 首次启动后会在 `plugins/AutoTotem/` 生成配置文件

## 📋 命令

| 命令 | 说明 | 权限 |
| --- | --- | --- |
| `/autototem enabled true\|false` | 开启/关闭自动图腾（无参数时查询当前状态） | `autototem.use` |
| `/autototem cooldowntime [tick]` | 查看/设置触发冷却时间（20 tick = 1 秒），不能低于服务器默认值 | `autototem.use` |
| `/autototem setdefaultcooldown [tick]` | 查看/设置服务器默认冷却时间（保存至 config.yml） | `autototem.admin` |
| `/autototem reload` | 重新加载配置文件 | `autototem.reload` |
| `/autototem help` | 显示帮助信息 | `autototem.use` |

别名：`/autototem` → `/at`；`/autototem cooldowntime` → `/at cd`；`/autototem setdefaultcooldown` → `/at sdc`

## 🔑 权限

| 权限 | 默认值 | 说明 |
| --- | --- | --- |
| `autototem.use` | `true` | 允许使用 `/autototem` 命令 |
| `autototem.reload` | `op` | 允许使用 `/autototem reload` 重新加载配置 |
| `autototem.admin` | `op` | 允许使用 `/autototem setdefaultcooldown` 修改服务器默认冷却 |

## ⚙️ 配置

配置文件位于 `plugins/AutoTotem/config.yml`：

```yaml
# 玩家默认是否启用自动图腾
default-enabled: true
# 默认触发冷却时间（单位：tick，20 tick = 1 秒）
default-cooldown-ticks: 0
# 是否在被玩家攻击造成的致命伤害时触发自动图腾（PVP 场景）
pvp-enabled: true
# 默认语言（当玩家客户端语言不在 lang/ 目录时使用，例如 en-US、zh-CN）
default-locale: en-US
```

## 🌍 本地化

AutoTotem 会在运行时加载 `lang/` 目录下的语言文件——只需放入一个新的 `.lang` 文件（例如 `de-DE.lang`）即可添加一种新语言，无需修改任何代码。

我们非常欢迎本地化贡献！如果你想添加或改进某种语言的翻译，欢迎提交 Pull Request，附上新增或更新的 `.lang` 文件即可。

## 🛠 构建

项目采用多模块构建，最终将各版本实现合并为一个**通用 jar**：

```bash
# 构建通用 jar
./gradlew build

# 产物：build/libs/AutoTotem-<平台>-<版本>.jar
# 例如 AutoTotem-Plugin-Paper-1.1.2.jar
```

### 工作原理

- `common`——共享插件代码（主类、监听器、命令、消息），针对 Paper 1.20.1 API 用 **JDK 17** 编译
- `paper-1-20` / `paper-1-21` / `paper-26-1` / `paper-26-2`——每个模块针对**各自的 Paper API**（JDK 17 / 21 / 25）编译一个小型版本适配器，处理版本 API 差异（如 1.20.5 起 `Particle.TOTEM` 更名为 `TOTEM_OF_UNDYING`）
- 根项目通过 Shadow 插件将所有模块合并成一个 jar；jar 名称由 `gradle.properties` 中的 `version` 与 `serverPlatform` 决定

运行时插件自动检测服务器版本，通过反射加载对应版本的适配器——**一个 jar 即可在 1.20.1 到 26.2 上运行**。

构建所需 JDK：

- `common` / `paper-1-20`：**JDK 17**
- `paper-1-21`：**JDK 21**
- `paper-26-1` / `paper-26-2` / 根项目：**JDK 25**

安装所需 JDK 后，在 `gradle.properties` 中通过 `org.gradle.java.installations.paths` 指定其路径（逗号分隔，使用正斜杠）。

```bash
# 构建全部模块并合并为一个 jar
./gradlew build

# 本地启动 Paper 服务器进行测试（1.20.1）
./gradlew runServer
```

## 📄 许可证

本项目使用 [带非商用与禁止闭源再分发条款的 MIT 许可证](LICENSE)——并非标准 MIT 协议。使用、修改或再分发前，请完整阅读 [LICENSE](LICENSE) 文件中的条款。

- **非商用**：商用须事先获得版权所有者的书面许可
- **仅开源分发**：任何再分发或衍生作品必须以源码形式、并在相同许可证下发布


## 免责声明 & 注意事项


> [!CAUTION]
> 本插件为**仅服务端插件**，完全由服务器管理员安装与管控。插件修改Minecraft原版机制，实现不死图腾自动消耗。**该自动化物品消耗逻辑可能造成游戏平衡性问题，在PVP、竞技、赛事服务器上有可能被认定为不公平优势。**
>
> 1. 本插件按现状提供，不提供任何担保。因服主安装、配置本插件所带来的玩家纠纷、规则问题、处罚封禁等一切后果，均由服务器管理员承担，开发者不承担相关责任。
> 2. 服务器管理员在启用前，**有独立责任评估该功能是否符合服务器规则与公平游戏准则**；权限管控、冷却参数、特定游戏模式下是否关闭功能等配置决策全部由服主负责。
> 3. 自动图腾消耗会对比依赖手动切换图腾的玩家产生游戏失衡，竞技/PVP场景请审慎决定是否开启。
> 4. 第三方反作弊插件可能因本插件的服务端自动化行为产生误报，正式投入使用前务必完成兼容性测试。
> 5. 使用本插件的全部风险由服务器运营方自行承担。