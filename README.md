# Tokimi's Toolshed – Forge 1.20.1 port

Port of **Tokimi's Toolshed v1.3** (Fabric, MC 1.20.1) to **Minecraft Forge 1.20.1 (47.x)**.
Original mod by CozyStudios, CC0-1.0: https://github.com/CozyCord/TokimisToolshed

## Building
Push to GitHub – the workflow in `.github/workflows/build.yml` builds with JDK 17 and uploads
`build/libs/tokimistoolshed-1.3.jar` as an artifact.
Locally: `./gradlew build` (JDK 17).

## What changed vs. the Fabric version
| Fabric | Forge |
|---|---|
| Yarn mappings, Fabric API | Official (Mojang) mappings, Forge events |
| `AttackBlockCallback` | `PlayerInteractEvent.LeftClickBlock` |
| `PlayerBlockBreakEvents.AFTER` (creative) | `BlockEvent.BreakEvent` |
| `FuelRegistry` | `FurnaceFuelBurnTimeEvent` |
| `CowMilkMixin` | `PlayerInteractEvent.EntityInteract` |
| `ShearsLootMixin` | Global loot modifier `tokimistoolshed:clippers_as_shears` |
| `WorldRenderEvents` / `HudRenderCallback` | `RenderLevelStageEvent` / `RegisterGuiOverlaysEvent` |
| Fabric load conditions on compat | `forge:mod_loaded` recipe/advancement conditions |

Not ported: `ItemIdMigrationMixin` (renamed-ID migration for old Fabric worlds; irrelevant to new Forge worlds).
Clippers loot: only Clippers are redirected to shears loot (the Fabric mixin affected any non-vanilla shears).
`minecraft:stone_tool_materials` (not in 1.20.1) was replaced with an explicit cobblestone/blackstone/cobbled deepslate list.
