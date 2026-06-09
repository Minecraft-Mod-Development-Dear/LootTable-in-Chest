package com.minecraftmoddevelopmentdear.loottablechesttool;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(
    modid = LootTableChestTool.MODID,
    name = LootTableChestTool.NAME,
    version = LootTableChestTool.VERSION,
    acceptedMinecraftVersions = "[1.12.2]"
)
public final class LootTableChestTool {
    public static final String MODID = "loottablechesttool";
    public static final String NAME = "Loot Table Chest Tool";
    public static final String VERSION = "1.0.0";

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new LootChestCommand());
    }
}
