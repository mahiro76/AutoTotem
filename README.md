# AutoTotem

[![license](https://img.shields.io/badge/license-MIT-34a853?style=flat-square)](https://github.com/mahiro76/AutoTotem/blob/master/LICENSE)


**🌐 Language / 语言:** [English](README.md) · [简体中文](README_zh-CN.md)

A **Paper** Minecraft server plugin that automatically consumes totems of undying from your inventory when you take a fatal hit, saving you from death. Works on a single jar across Minecraft **1.20.1 – 26.2**.

## ✨ Features

- **Auto-consume totems**: Consumes a totem of undying from the player's inventory and cancels the damage when taking a fatal hit
- **Vanilla totem effects**: Replicates the vanilla totem — restores 1 HP, clears fire and potion effects, applies Regeneration II, Absorption IV and Fire Resistance I, with the original sound and particle effects
- **Search order**: Offhand → Main hand → Rest of the inventory
- **Per-player settings**: Toggle state and cooldown are stored per player
- **Trigger cooldown**: Set a cooldown to prevent repeated triggers
- **Fatal damage check**: Only triggers when the damage would be lethal; void damage never triggers; absorption is included in the calculation
- **PVP toggle**: Optionally disable auto-totem for player-caused lethal damage (`pvp-enabled` config)
- **Multi-language**: Built-in Simplified Chinese (zh-CN) and English (en-US), auto-selected based on the player's client language
- **Tab completion**: Full tab-completion support for all commands, filtered by permission

## 📥 Installation

1. Download the latest `AutoTotem-*.jar` from [modrinth](https://modrinth.com/plugin/autototem-plugin)
2. Drop the jar into the server's `plugins/` folder
3. Restart the server (or load it via a plugin manager)
4. A config file will be generated in `plugins/AutoTotem/` on first startup

## 📋 Commands

| Command | Description | Permission |
| --- | --- | --- |
| `/autototem enabled true\|false` | Enable/disable auto totem (no argument shows current status) | `autototem.use` |
| `/autototem cooldowntime [tick]` | View/set trigger cooldown (20 ticks = 1 second); cannot be set below the server default | `autototem.use` |
| `/autototem setdefaultcooldown [tick]` | View/set server default cooldown (saved to config.yml) | `autototem.admin` |
| `/autototem reload` | Reload the configuration file | `autototem.reload` |
| `/autototem help` | Show help information | `autototem.use` |

Aliases: `/autototem` → `/at`; `/autototem cooldowntime` → `/at cd`; `/autototem setdefaultcooldown` → `/at sdc`

## 🔑 Permissions

| Permission | Default | Description |
| --- | --- | --- |
| `autototem.use` | `true` | Allows using the `/autototem` command |
| `autototem.reload` | `op` | Allows reloading the configuration with `/autototem reload` |
| `autototem.admin` | `op` | Allows changing the server default cooldown with `/autototem setdefaultcooldown` |

## ⚙️ Configuration

Config file location: `plugins/AutoTotem/config.yml`

```yaml
# Whether players have auto totem enabled by default
default-enabled: true
# Default trigger cooldown (in ticks, 20 ticks = 1 second)
default-cooldown-ticks: 0
# Whether to trigger auto totem on lethal damage caused by another player (PvP)
pvp-enabled: true
# Default locale (used when the player's client language is not in the lang/ folder, e.g. en-US, zh-CN)
default-locale: en-US
```

## 🌍 Localization

AutoTotem loads language files from the `lang/` folder at runtime — adding a new language is as simple as dropping in a new `.lang` file (e.g. `de-DE.lang`), no code changes needed.

We welcome localization contributions! If you want to add or improve a translation, feel free to submit a pull request with a new or updated `.lang` file.

## 🛠 Building

The project is a multi-module build that merges all version-specific implementations into **a single universal jar**:

```bash
# Build the universal jar
./gradlew build

# Output: build/libs/AutoTotem-<platform>-<version>.jar
# e.g. AutoTotem-Plugin-Paper-1.1.2.jar
```

### How it works

- `common` — shared plugin code (main class, listener, commands, messages), compiled against Paper 1.20.1 API with **JDK 17**
- `paper-1-20` / `paper-1-21` / `paper-26-1` / `paper-26-2` — each module compiles a small version adapter against its **own Paper API** (JDK 17 / 21 / 25), handling API differences like `Particle.TOTEM` (renamed to `TOTEM_OF_UNDYING` in 1.20.5)
- The root project merges all modules into one jar via the Shadow plugin; the jar name is driven by `gradle.properties` (`version` and `serverPlatform`)

At runtime, the plugin detects the server version and loads the matching adapter class via reflection — so **a single jar works on 1.20.1 through 26.2**.

Build requirements:

- `common` / `paper-1-20`: **JDK 17**
- `paper-1-21`: **JDK 21**
- `paper-26-1` / `paper-26-2` / root: **JDK 25**

Install the required JDKs and point Gradle at them via `org.gradle.java.installations.paths` in `gradle.properties` (comma-separated, forward slashes).

```bash
# Build all modules and merge into one jar
./gradlew build

# Start a local Paper server for testing (1.20.1)
./gradlew runServer
```

## 📄 License

This project is licensed under the [MIT License with Non-Commercial and No Closed-Source Redistribution Clauses](LICENSE) — not the standard MIT license. Please read the [LICENSE](LICENSE) file for the full terms before using, modifying, or redistributing this project.

- **Non-Commercial**: Commercial use requires prior written permission from the copyright holder
- **Source-Code Only**: Any redistribution or derivative works must be distributed in source code form under the same license


## Disclaimer & Precautions

>[!CAUTION]
> This is a **server‑side only plugin**, installed and controlled entirely by server administrators. It modifies vanilla Minecraft mechanics by automatically consuming Totems of Undying. **Automated item‑consumption logic may create gameplay imbalances and may be considered an unfair advantage, especially on PvP, competitive or tournament servers.**
> 1. This plugin is provided **as‑is** without any form of warranty. The developer assumes no liability for player disputes, rule‑related issues, sanctions, bans or any other consequences resulting from server administrators installing or configuring this plugin.
> 2. It is the **server administrator’s sole responsibility** to assess whether this feature aligns with server rules and fair‑play expectations before enabling it. Administrators are responsible for making configuration decisions such as permission restrictions, cooldown values and whether to disable the feature for specific game modes.
> 3. Automated totem consumption may create imbalance against players who rely on manual totem usage. For competitive or PvP scenarios, administrators should carefully consider whether to enable this functionality.
> 4. Third‑party anti‑cheat plugins may trigger false positives caused by this plugin’s server‑side automatic behaviour. Compatibility testing is strongly recommended before production deployment.
> 5. This plugin shall only be operated at the server operator’s own risk.