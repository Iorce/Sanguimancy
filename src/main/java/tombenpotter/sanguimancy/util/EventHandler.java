package tombenpotter.sanguimancy.util;

import WayofTime.alchemicalWizardry.api.event.RitualActivatedEvent;
import WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler;
import WayofTime.alchemicalWizardry.common.items.EnergyItems;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import org.lwjgl.input.Keyboard;
import tombenpotter.sanguimancy.Sanguimancy;
import tombenpotter.sanguimancy.network.EventCorruptedInfusion;
import tombenpotter.sanguimancy.network.PacketHandler;
import tombenpotter.sanguimancy.network.PacketPlayerSearch;
import tombenpotter.sanguimancy.registry.ItemsRegistry;

public class EventHandler {

    public EventHandler() {
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(Sanguimancy.modid)) {
            ConfigHandler.syncConfig();
            Sanguimancy.logger.info(StatCollector.translateToLocal("info." + Sanguimancy.modid + ".console.config.refresh"));
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.entity != null && !event.entity.worldObj.isRemote) {
            if (event.entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) event.entity;
                String owner = player.getCommandSenderName();
                int currentEssence = SoulNetworkHandler.getCurrentEssence(owner);
                if (event.source.getEntity() != null && event.source.getEntity() instanceof EntityPlayer) {
                    EntityPlayer perpetrator = (EntityPlayer) event.source.getEntity();
                    ItemStack attunedStack = new ItemStack(ItemsRegistry.playerSacrificer, 1, 1);
                    if (perpetrator.inventory.hasItemStack(attunedStack)) {
                        perpetrator.inventory.consumeInventoryItem(attunedStack.getItem());
                        ItemStack focusedStack = new ItemStack(ItemsRegistry.playerSacrificer, 1, 2);
                        EnergyItems.checkAndSetItemOwner(focusedStack, owner);
                        focusedStack.stackTagCompound.setInteger("bloodStolen", currentEssence);
                        focusedStack.stackTagCompound.setString("thiefName", perpetrator.getCommandSenderName());
                        perpetrator.inventory.addItemStackToInventory(focusedStack);
                        SoulNetworkHandler.setCurrentEssence(owner, 0);
                        NBTTagCompound tag = SoulCorruptionHelper.getModTag(player, Sanguimancy.modid);
                        SoulCorruptionHelper.incrementCorruption(tag);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent event) {
        if (!event.entity.worldObj.isRemote && event.entity != null && event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            NBTTagCompound tag = SoulCorruptionHelper.getModTag(player, Sanguimancy.modid);
            if (SoulCorruptionHelper.getCorruptionLevel(tag) > 0) return;
            else SoulCorruptionHelper.negateCorruption(tag);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        NBTTagCompound tag = SoulCorruptionHelper.getModTag(event.player, Sanguimancy.modid);
        if (SoulCorruptionHelper.isCorruptionOver(tag, 10)) SoulCorruptionHelper.spawnChickenFollower(event.player);
        if (SoulCorruptionHelper.isCorruptionOver(tag, 25)) SoulCorruptionHelper.killGrass(event.player);
        if (SoulCorruptionHelper.isCorruptionOver(tag, 40)) SoulCorruptionHelper.hurtAndHealAnimals(event.player);
        if (SoulCorruptionHelper.isCorruptionOver(tag, 50)) SoulCorruptionHelper.spawnIllusion(event.player);
        if (SoulCorruptionHelper.isCorruptionOver(tag, 70)) SoulCorruptionHelper.randomTeleport(event.player);
    }

    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event) {
        if (event.entityPlayer != null && event.target != null && event.target instanceof EntityLivingBase) {
            NBTTagCompound tag = SoulCorruptionHelper.getModTag(event.entityPlayer, Sanguimancy.modid);
            EntityLivingBase target = (EntityLivingBase) event.target;
            if (SoulCorruptionHelper.isCorruptionOver(tag, 30)) SoulCorruptionHelper.addWither(target);
        }
    }

    @SubscribeEvent
    public void onCorruptedInfusion(EventCorruptedInfusion.EventPlayerCorruptedInfusion event) {
    }

    @SubscribeEvent
    public void onRitualActivation(RitualActivatedEvent event) {
        if (event.player != null) {
            NBTTagCompound tag = SoulCorruptionHelper.getModTag(event.player, Sanguimancy.modid);
            if (SoulCorruptionHelper.isCorruptionOver(tag, 15) && event.player.worldObj.rand.nextInt(10) == 0) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onChunkForce(ForgeChunkManager.ForceChunkEvent event) {
        if (!Loader.isModLoaded("loaderlist")) {
            RandomUtils.writeLog(event.ticket.getModId() + " forcing the loading of the chunk at x= " + String.valueOf(event.location.getCenterXPos()) + " and z=" + String.valueOf(event.location.getCenterZPosition()) + " in dimension " + String.valueOf(event.ticket.world.provider.dimensionId), "ChunkloadingLog.txt");
        }
    }

    @SubscribeEvent
    public void onChunkUnforce(ForgeChunkManager.UnforceChunkEvent event) {
        if (!Loader.isModLoaded("loaderlist")) {
            RandomUtils.writeLog(event.ticket.getModId() + " unforcing the loading of the chunk at x= " + String.valueOf(event.location.getCenterXPos()) + " and z=" + String.valueOf(event.location.getCenterZPosition()) + " in dimension " + String.valueOf(event.ticket.world.provider.dimensionId), "ChunkloadingLog.txt");
        }
    }

    public static class ClientEventHandler {
        public static KeyBinding keySearchPlayer = new KeyBinding("key.Sanguimancy.search", Keyboard.KEY_F, Sanguimancy.modid);

        public ClientEventHandler() {
            ClientRegistry.registerKeyBinding(keySearchPlayer);
        }

        @SubscribeEvent
        public void onKeyInput(InputEvent.KeyInputEvent event) {
            if (keySearchPlayer.isPressed()) {
                PacketHandler.INSTANCE.sendToServer(new PacketPlayerSearch());
            }
        }
    }
}
