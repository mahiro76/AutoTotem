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

1. Download the latest `AutoTotem-*.jar` from [modrinth](https://modrinth.com/plugin/autototem-plugin)
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

Requires **JDK 17+** (JDK 21 recommended).

```bash
# Build the plugin
./gradlew build

# Build output is located in build/libs/
# Start a local Paper server for testing
./gradlew runServer
```

## 📄 License

This project is licensed under the [MIT License with Non-Commercial and No Closed-Source Redistribution Clauses](LICENSE) — not the standard MIT license. Please read the [LICENSE](LICENSE) file for the full terms before using, modifying, or redistributing this project.

- **Non-Commercial**: Commercial use requires prior written permission from the copyright holder
- **Source-Code Only**: Any redistribution or derivative works must be distributed in source code form under the same license
