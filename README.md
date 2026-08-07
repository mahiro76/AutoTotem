# AutoTotem

[![License: MIT-NC-ND-Source](https://img.shields.io/badge/License-MIT--NC--ND--Source-yellow)](LICENSE)

**🌐 Language / 语言:** [English](README.md) · [简体中文](README_zh-CN.md)

A **Paper** Minecraft server plugin that automatically consumes totems of undying from your inventory when you take a fatal hit, saving you from death.

## ✨ Features

- **Auto-consume totems**: Consumes a totem of undying from the player's inventory and cancels the damage when taking a fatal hit
- **Vanilla totem effects**: Replicates the vanilla totem — restores 1 HP, clears fire and potion effects, applies Regeneration II, Absorption IV and Fire Resistance I, with the original sound and particle effects
- **Search order**: Offhand → Main hand → Rest of the inventory
- **Per-player settings**: Toggle state and cooldown are stored per player
- **Trigger cooldown**: Set a cooldown to prevent repeated triggers
- **Fatal damage check**: Only triggers when the damage would be lethal; void damage never triggers; absorption is included in the calculation
- **Multi-language**: Built-in Simplified Chinese (zh-CN) and English (en-US), auto-selected based on the player's client language
- **Tab completion**: Full tab-completion support for all commands

## 📥 Installation

1. Download the latest `AutoTotem-*.jar`
2. Drop the jar into the server's `plugins/` folder
3. Restart the server (or load it via a plugin manager)
4. A config file will be generated in `plugins/AutoTotem/` on first startup

## 📋 Commands

| Command | Description | Permission |
| --- | --- | --- |
| `/autototem enabled true\|false` | Enable/disable auto totem (no argument shows current status) | `autototem.use` |
| `/autototem cooldowntime [tick]` | View/set trigger cooldown (20 ticks = 1 second) | `autototem.use` |
| `/autototem reload` | Reload the configuration file | `autototem.use` |
| `/autototem help` | Show help information | `autototem.use` |

## 🔑 Permissions

| Permission | Default | Description |
| --- | --- | --- |
| `autototem.use` | `true` | Allows using the `/autototem` command |

## ⚙️ Configuration

Config file location: `plugins/AutoTotem/config.yml`

```yaml
# Whether players have auto totem enabled by default
default-enabled: true
# Default trigger cooldown (in ticks, 20 ticks = 1 second)
default-cooldown-ticks: 0
# Default locale (used when the player's client language is not in the lang/ folder, e.g. en-US, zh-CN)
default-locale: en-US
```

## 🌍 Localization

AutoTotem loads language files from the `lang/` folder at runtime — adding a new language is as simple as dropping in a new `.lang` file (e.g. `de-DE.lang`), no code changes needed.

We welcome localization contributions! If you want to add or improve a translation, feel free to submit a pull request with a new or updated `.lang` file.

## 🛠 Building

The project is a multi-module build that produces a separate jar for each supported Minecraft version line:

| Module | Target server | Jar |
| --- | --- | --- |
| `paper-1-20` | 1.20.1+ | `paper-1-20/build/libs/AutoTotem-1.20.jar` |
| `paper-1-21` | 1.21.x | `paper-1-21/build/libs/AutoTotem-1.21.jar` |
| `paper-26-1` | 26.1.x | `paper-26-1/build/libs/AutoTotem-26.1.jar` |
| `paper-26-2` | 26.2.x | `paper-26-2/build/libs/AutoTotem-26.2.jar` |

> Each version module builds against its own Paper API and declares the matching `api-version` in `plugin.yml`, so newer versions (1.20.5+, where `Particle.TOTEM` was renamed to `TOTEM_OF_UNDYING`) are handled correctly.

Build requirements (per version module):

- `paper-1-20`: **JDK 17**
- `paper-1-21`: **JDK 21**
- `paper-26-1` / `paper-26-2`: **JDK 25**

Install the required JDKs and point Gradle at them via `org.gradle.java.installations.paths` in `gradle.properties` (semicolon-separated, forward slashes).

```bash
# Build all version jars
./gradlew build

# Or build a single version (e.g. 1.21)
./gradlew :paper-1-21:build

# Build output is located in <module>/build/libs/
# Start a local Paper server for testing (e.g. 1.21)
./gradlew :paper-1-21:runServer
```

## 📄 License

This project is licensed under the [MIT License with Non-Commercial and No Closed-Source Redistribution Clauses](LICENSE) — not the standard MIT license. Please read the [LICENSE](LICENSE) file for the full terms before using, modifying, or redistributing this project.

- **Non-Commercial**: Commercial use requires prior written permission from the copyright holder
- **Source-Code Only**: Any redistribution or derivative works must be distributed in source code form under the same license
