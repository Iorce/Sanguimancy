package tombenpotter.sanguimancy.registry;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import tombenpotter.sanguimancy.items.*;
import tombenpotter.sanguimancy.items.corrupted.ItemCorruptedAxe;
import tombenpotter.sanguimancy.items.corrupted.ItemCorruptedPickaxe;
import tombenpotter.sanguimancy.items.corrupted.ItemCorruptedShovel;
import tombenpotter.sanguimancy.items.corrupted.ItemCorruptedSword;
import tombenpotter.sanguimancy.util.RandomUtils;

public class ItemsRegistry {

    public static Item playerSacrificer;
    public static Item soulCorruptionTest;
    public static Item corruptedDemonShard;
    public static Item corruptionCatalyst;
    public static Item oreLump;
    public static Item bloodAmulet;
    public static Item wand;
    public static Item chunkClaimer;
    public static Item corruptedSword;
    public static Item corruptedPickaxe;
    public static Item corruptedShovel;
    public static Item corruptedAxe;
    public static Item playerGuide;
    public static Item corruptedMineral;
    public static Item imbuedStick;

    public static void registerItems() {
        playerSacrificer = new ItemPlayerSacrificer();
        GameRegistry.registerItem(playerSacrificer, "playerSacrificer");

        soulCorruptionTest = new ItemSoulCorruptionTest();
        GameRegistry.registerItem(soulCorruptionTest, "soulCorruptionTest");

        corruptedDemonShard = new ItemCorruptedDemonShard();
        GameRegistry.registerItem(corruptedDemonShard, "corruptedDemonShard");

        corruptionCatalyst = new ItemCorruptionCatalyst();
        GameRegistry.registerItem(corruptionCatalyst, "corruptionCatalist");

        oreLump = new ItemOreLump();
        GameRegistry.registerItem(oreLump, "oreLump");

        bloodAmulet = new ItemBloodAmulet();
        GameRegistry.registerItem(bloodAmulet, "bloodAmulet");

        wand = new ItemWand();
        GameRegistry.registerItem(wand, "wand");

        chunkClaimer = new ItemChunkClaimer();
        GameRegistry.registerItem(chunkClaimer, "chunkClaimer");

        corruptedSword = new ItemCorruptedSword(32);
        GameRegistry.registerItem(corruptedSword, "corruptedSword");

        corruptedPickaxe = new ItemCorruptedPickaxe(RandomUtils.corruptedMaterial);
        GameRegistry.registerItem(corruptedPickaxe, "corruptedPickaxe");

        corruptedShovel = new ItemCorruptedShovel(RandomUtils.corruptedMaterial);
        GameRegistry.registerItem(corruptedShovel, "corruptedShovel");

        corruptedAxe = new ItemCorruptedAxe(RandomUtils.corruptedMaterial);
        GameRegistry.registerItem(corruptedAxe, "corruptedAxe");

        playerGuide = new ItemPlayerGuide();
        GameRegistry.registerItem(playerGuide, "playerGuide");

        corruptedMineral = new ItemCorruptedMineral();
        GameRegistry.registerItem(corruptedMineral, "corruptedMineral");

        imbuedStick = new ItemImbuedStick();
        GameRegistry.registerItem(imbuedStick, "imbuedStick");
    }
}
