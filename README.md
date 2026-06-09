# Loot Table Chest Tool / LOOTTABLEINCHEST

## English

This is a Minecraft Forge 1.12.2 debug helper mod. It places chests with selected loot tables in front of the command sender, making loot-table testing faster and more repeatable.

### Overview

- Place chests for a specific loot table ID.
- Use `chests`, `drops`, or `all` to test groups of known loot tables.
- Use `count` to place multiple chests for each matched loot table.
- If `count` is omitted, it defaults to `0`, so the command only reports how many tables matched.
- The maximum count is 1000 chests per loot table.

### Requirements

- Minecraft: `1.12.2`
- Minecraft Forge: `1.12.2-14.23.5.2847`
- Java: `8`
- Mod ID: `loottablechesttool`

### Command

```text
/lootchest <lootTable|all|chests|drops> [count]
```

This command requires permission level 2. Use it in a single-player world with cheats enabled, or as an operator on a server.

#### Arguments

| Argument | Description |
| --- | --- |
| `lootTable` | A loot table ID such as `minecraft:chests/simple_dungeon`. |
| `all` | Matches all known loot tables that can be discovered. |
| `chests` | Matches loot tables whose path contains `chests/`. |
| `drops` | Matches loot tables whose path contains `entities/`. `entities` works as an alias. |
| `count` | Number of chests to place per loot table. Defaults to `0`. |

### Examples

```text
/lootchest minecraft:chests/simple_dungeon 10
/lootchest minecraft:entities/witch 100
/lootchest chests 5
/lootchest drops 20
/lootchest all 1
```

`/lootchest minecraft:chests/simple_dungeon 10` places 10 chests using the simple dungeon loot table.

`/lootchest chests 5` places 5 chests for each matched chest-type loot table.

`/lootchest all 1` places 1 chest for every known loot table. This can create many chests, so a test world is recommended.

### Placement

Chests are placed in a grid starting 1 block in front of the command sender. The grid uses 32 columns, with 2 blocks of spacing between chest positions.

### Notes

- This mod is intended for debugging, verification, and mod-development support.
- The command places chests into the world, so be careful around existing builds.
- `all` and large `count` values can create a large number of chests.
- Chest contents follow Minecraft loot-table behavior and are generated when the chest is opened.

### Build

```text
gradle build
```

The built jar is generated under `build/libs/`.

## 日本語

Minecraft Forge 1.12.2 用のデバッグ補助 MOD です。指定したルートテーブルを設定したチェストを、プレイヤーの向いている方向の前方にまとめて配置できます。

### 概要

- ルートテーブル ID を直接指定して、そのテーブルを持つチェストを配置できます。
- `chests`、`drops`、`all` を指定して、既知のルートテーブルをまとめてテストできます。
- `count` を指定すると、各ルートテーブルごとに複数個のチェストを配置します。
- `count` を省略した場合は `0` として扱われ、チェストを置かずに一致したテーブル数だけを表示します。
- 配置数は 1 つのルートテーブルにつき最大 1000 個です。

### 対応環境

- Minecraft: `1.12.2`
- Minecraft Forge: `1.12.2-14.23.5.2847`
- Java: `8`
- Mod ID: `loottablechesttool`

### コマンド

```text
/lootchest <lootTable|all|chests|drops> [count]
```

このコマンドは権限レベル 2 が必要です。シングルプレイではチートを有効にしたワールド、またはサーバーでは OP 権限のあるユーザーで実行してください。

#### 引数

| 引数 | 説明 |
| --- | --- |
| `lootTable` | `minecraft:chests/simple_dungeon` のようなルートテーブル ID を指定します。 |
| `all` | 取得できる既知のルートテーブルをすべて対象にします。 |
| `chests` | パスに `chests/` を含むルートテーブルを対象にします。 |
| `drops` | パスに `entities/` を含むルートテーブルを対象にします。`entities` も同じ意味で使えます。 |
| `count` | 各ルートテーブルごとに配置するチェスト数です。省略時は `0`。 |

### 使用例

```text
/lootchest minecraft:chests/simple_dungeon 10
/lootchest minecraft:entities/witch 100
/lootchest chests 5
/lootchest drops 20
/lootchest all 1
```

`/lootchest minecraft:chests/simple_dungeon 10` は、シンプルダンジョンのルートテーブルを設定したチェストを 10 個配置します。

`/lootchest chests 5` は、チェスト系のルートテーブルそれぞれにつき 5 個ずつチェストを配置します。

`/lootchest all 1` は、取得できるすべての既知ルートテーブルを 1 個ずつ配置します。数が多くなる場合があるため、テスト用ワールドでの使用をおすすめします。

### 配置ルール

チェストは実行者の 1 ブロック前を起点に、向いている方向を基準として横 32 列のグリッド状に配置されます。各チェストの間隔は 2 ブロックです。

### 注意

- この MOD はデバッグ、検証、MOD 開発補助を目的としています。
- 既存ブロックの上にチェストを配置するため、実行場所には注意してください。
- `all` や大きい `count` は大量のチェストを生成します。
- チェストの中身は Minecraft のルートテーブル仕様に従い、チェストを開いたときに生成されます。

### ビルド

```text
gradle build
```

ビルド後の jar は `build/libs/` に出力されます。
