package com.minecraftmoddevelopmentdear.loottablechesttool;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockChest;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootTableList;

public final class LootChestCommand extends CommandBase {
    private static final int DEFAULT_COUNT = 0;
    private static final int MAX_COUNT_PER_TABLE = 1000;
    private static final int COLUMNS = 32;

    @Override
    public String getName() {
        return "lootchest";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/lootchest <lootTable|all|chests|drops> [count]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            throw new CommandException(getUsage(sender));
        }

        World world = sender.getEntityWorld();
        if (!(world instanceof WorldServer)) {
            throw new CommandException("This command must be run in a server world.");
        }

        String selector = args[0];
        int count = args.length >= 2 ? parseInt(args[1], 0, MAX_COUNT_PER_TABLE) : DEFAULT_COUNT;
        List<ResourceLocation> lootTables = resolveLootTables(selector);
        if (lootTables.isEmpty()) {
            throw new CommandException("No loot tables matched: " + selector);
        }
        if (count == 0) {
            sender.sendMessage(new TextComponentString("[lootchest] matched " + lootTables.size()
                + " loot table(s); count=0, no chests placed."));
            return;
        }

        EnumFacing forward = getHorizontalFacing(sender);
        EnumFacing right = forward.rotateY();
        BlockPos origin = sender.getPosition().offset(forward);
        Random random = new Random(((WorldServer) world).getSeed() ^ System.nanoTime());

        int placed = 0;
        for (ResourceLocation lootTable : lootTables) {
            for (int i = 0; i < count; i++) {
                BlockPos pos = positionFor(origin, forward, right, placed);
                placeLootChest((WorldServer) world, pos, forward, lootTable, random.nextLong());
                placed++;
            }
        }

        sender.sendMessage(new TextComponentString("[lootchest] placed " + placed + " chest(s) for "
            + lootTables.size() + " loot table(s)."));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            List<String> options = new ArrayList<String>();
            options.add("all");
            options.add("chests");
            options.add("drops");
            options.add("entities");
            options.addAll(commonLootTables());
            return getListOfStringsMatchingLastWord(args, options);
        }
        return Collections.emptyList();
    }

    private static EnumFacing getHorizontalFacing(ICommandSender sender) {
        if (sender.getCommandSenderEntity() == null) {
            return EnumFacing.NORTH;
        }
        return sender.getCommandSenderEntity().getHorizontalFacing();
    }

    private static BlockPos positionFor(BlockPos origin, EnumFacing forward, EnumFacing right, int index) {
        int column = index % COLUMNS;
        int row = index / COLUMNS;
        return origin.offset(right, column * 2).offset(forward, row * 2);
    }

    private static void placeLootChest(WorldServer world, BlockPos pos, EnumFacing facing, ResourceLocation lootTable,
        long seed) {
        world.setBlockState(pos, Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, facing), 3);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityChest) {
            ((TileEntityChest) tileEntity).setLootTable(lootTable, seed);
            tileEntity.markDirty();
        }
    }

    private static List<ResourceLocation> resolveLootTables(String selector) throws CommandException {
        String normalized = selector.toLowerCase();
        List<ResourceLocation> all = allKnownLootTables();
        if ("all".equals(normalized) || "all_loot".equals(normalized) || "alltables".equals(normalized)) {
            return all;
        }
        if ("chests".equals(normalized) || "chest_all".equals(normalized) || "all_chests".equals(normalized)) {
            return filter(all, "chests/");
        }
        if ("drops".equals(normalized) || "drop_all".equals(normalized) || "entities".equals(normalized)
            || "entities_all".equals(normalized) || "all_drops".equals(normalized)) {
            return filter(all, "entities/");
        }
        return Collections.singletonList(new ResourceLocation(selector));
    }

    private static List<ResourceLocation> filter(List<ResourceLocation> lootTables, String pathPart) {
        List<ResourceLocation> filtered = new ArrayList<ResourceLocation>();
        for (ResourceLocation lootTable : lootTables) {
            if (path(lootTable).contains(pathPart)) {
                filtered.add(lootTable);
            }
        }
        return filtered;
    }

    private static String path(ResourceLocation id) {
        String raw = id.toString();
        int sep = raw.indexOf(':');
        return sep >= 0 ? raw.substring(sep + 1) : raw;
    }

    private static List<ResourceLocation> allKnownLootTables() throws CommandException {
        Set<ResourceLocation> lootTables = new LinkedHashSet<ResourceLocation>();
        addLootTablesFromGetAll(lootTables);
        addLootTablesFromFields(lootTables);
        List<ResourceLocation> sorted = new ArrayList<ResourceLocation>(lootTables);
        Collections.sort(sorted, new Comparator<ResourceLocation>() {
            @Override
            public int compare(ResourceLocation a, ResourceLocation b) {
                return a.toString().compareTo(b.toString());
            }
        });
        return sorted;
    }

    @SuppressWarnings("unchecked")
    private static void addLootTablesFromGetAll(Set<ResourceLocation> lootTables) throws CommandException {
        try {
            Method getAll = LootTableList.class.getMethod("getAll");
            Object result = getAll.invoke(null);
            if (result instanceof Collection) {
                for (Object value : (Collection<Object>) result) {
                    if (value instanceof ResourceLocation) {
                        lootTables.add((ResourceLocation) value);
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            return;
        } catch (Exception e) {
            throw new CommandException("Failed to read LootTableList.getAll(): " + e.getMessage());
        }
    }

    private static void addLootTablesFromFields(Set<ResourceLocation> lootTables) throws CommandException {
        try {
            Field[] fields = LootTableList.class.getFields();
            for (Field field : fields) {
                if (ResourceLocation.class.isAssignableFrom(field.getType())) {
                    lootTables.add((ResourceLocation) field.get(null));
                }
            }
        } catch (Exception e) {
            throw new CommandException("Failed to read LootTableList fields: " + e.getMessage());
        }
    }

    private static List<String> commonLootTables() {
        List<String> values = new ArrayList<String>();
        values.add("minecraft:chests/simple_dungeon");
        values.add("minecraft:chests/abandoned_mineshaft");
        values.add("minecraft:chests/village_blacksmith");
        values.add("minecraft:chests/end_city_treasure");
        values.add("minecraft:entities/witch");
        return values;
    }
}
