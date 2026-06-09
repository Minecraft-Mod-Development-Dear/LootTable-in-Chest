# Loot Table Chest Tool

Minecraft Forge 1.12.2 debug helper mod for placing chests with selected loot tables.

## Command

```text
/lootchest <lootTable|all|chests|drops> [count]
```

Examples:

```text
/lootchest minecraft:chests/simple_dungeon 10
/lootchest minecraft:entities/witch 100
/lootchest chests 5
/lootchest drops 20
/lootchest all 1
```

`count` is per loot table. If omitted, it defaults to `0` and only reports the matched table count.
