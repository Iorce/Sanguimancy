package tombenpotter.sanguimancy.items.corrupted;

import WayofTime.alchemicalWizardry.api.items.interfaces.IBindable;
import WayofTime.alchemicalWizardry.common.items.EnergyItems;
import WayofTime.alchemicalWizardry.common.spell.complex.effect.SpellHelper;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import tombenpotter.sanguimancy.Sanguimancy;
import tombenpotter.sanguimancy.util.RandomUtils;
import tombenpotter.sanguimancy.util.SoulCorruptionHelper;

import java.util.List;

public class ItemCorruptedPickaxe extends ItemPickaxe implements IBindable {

    public final int minimumCorruption = 200;

    public ItemCorruptedPickaxe(ToolMaterial material) {
        super(material);
        setCreativeTab(Sanguimancy.tabSanguimancy);
        setUnlocalizedName(Sanguimancy.modid + ".corruptedPickaxe");
        setMaxDamage(0);
    }

    @Override
    public void registerIcons(IIconRegister ir) {
        super.registerIcons(ir);
        //TODO: Add an icon and a different overlay for every mode.
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase entityLivingBase) {
        if (block.getBlockHardness(world, x, y, z) >= 0 && !world.isRemote) {
            RandomUtils.checkAndSetCompound(stack);
            int toolMode = getToolMode(stack);
            int metadata = world.getBlockMetadata(x, y, z);
            int lpConsumption = 10;
            if (toolMode == 0) {
                RandomUtils.dropBlockDropsWithFortune(world, block, x, y, z, metadata, 0);
                world.setBlockToAir(x, y, z);
            } else if (toolMode == 1) {
                lpConsumption = lpConsumption * 5;
                RandomUtils.dropSilkDrops(world, block, x, y, z, metadata);
                world.setBlockToAir(x, y, z);
            } else if (toolMode == 2) {
                lpConsumption = lpConsumption * 5;
                RandomUtils.dropBlockDropsWithFortune(world, block, x, y, z, metadata, 1);
                world.setBlockToAir(x, y, z);
            } else if (toolMode == 3) {
                lpConsumption = lpConsumption * 10;
                RandomUtils.dropBlockDropsWithFortune(world, block, x, y, z, metadata, 2);
                world.setBlockToAir(x, y, z);
            } else if (toolMode == 4) {
                lpConsumption = lpConsumption * 20;
                RandomUtils.dropBlockDropsWithFortune(world, block, x, y, z, metadata, 3);
                world.setBlockToAir(x, y, z);
            } else if (toolMode == 5) {
                lpConsumption = lpConsumption * 15;
                RandomUtils.dropSmeltDrops(world, block, x, y, z, metadata);
                world.setBlockToAir(x, y, z);
            }
            if (entityLivingBase instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entityLivingBase;
                EnergyItems.syphonBatteries(stack, player, lpConsumption);
                if (player.getCommandSenderName().equals(RandomUtils.getItemOwner(stack)) && getToolMode(stack) != 0) {
                    NBTTagCompound tag = SoulCorruptionHelper.getModTag(player, Sanguimancy.modid);
                    SoulCorruptionHelper.incrementCorruption(player, tag);
                } else if (!player.getCommandSenderName().equals(RandomUtils.getItemOwner(stack))) {
                    player.setHealth(player.getMaxHealth() / 2);
                    player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("info.Sanguimancy.tooltip.wrong.player")));
                }
            }
        }
        return getToolMode(stack) <= 5;
    }

    public int getToolMode(ItemStack stack) {
        RandomUtils.checkAndSetCompound(stack);
        return stack.stackTagCompound.getInteger("ToolMode");
    }

    public void setToolMode(ItemStack stack, int mode) {
        RandomUtils.checkAndSetCompound(stack);
        stack.stackTagCompound.setInteger("ToolMode", mode);
    }

    public void nextToolMode(ItemStack stack) {
        RandomUtils.checkAndSetCompound(stack);
        if (stack.stackTagCompound.getInteger("ToolMode") + 1 <= 5) {
            setToolMode(stack, stack.stackTagCompound.getInteger("ToolMode") + 1);
        } else {
            stack.stackTagCompound.setInteger("ToolMode", 0);
            setToolMode(stack, 0);
        }
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        RandomUtils.checkAndSetCompound(stack);
        setToolMode(stack, 0);
        stack.stackTagCompound.setInteger("ToolMode", 0);
        super.onCreated(stack, world, player);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        if (!GuiScreen.isShiftKeyDown()) {
            list.add(StatCollector.translateToLocal("info.Sanguimancy.tooltip.shift.info"));
        } else {
            if (stack.hasTagCompound()) {
                list.add(StatCollector.translateToLocal("info.Sanguimancy.tooltip.pickaxe.mode") + ": " + tooltipForMode(stack.stackTagCompound.getInteger("ToolMode")));
                list.add(StatCollector.translateToLocal("info.Sanguimancy.tooltip.minimum.corruption.1"));
                list.add(StatCollector.translateToLocal("info.Sanguimancy.tooltip.minimum.corruption.2") + ": " + String.valueOf(minimumCorruption));
            }
        }
    }

    public String tooltipForMode(int modeID) {
        String mode = StatCollector.translateToLocal("info.Sanguimancy.tooltip.pickaxe.mode.regular");
        if (modeID == 1) {
            mode = StatCollector.translateToLocal("info.Sanguimancy.tooltip.pickaxe.mode.silk.touch");
        } else if (modeID == 2) {
            mode = StatCollector.translateToLocal("info.Sanguimancy.tooltip.pickaxe.mode.fortune.1");
        } else if (modeID == 3) {
            mode = StatCollector.translateToLocal("info.Sanguimancy.tooltip.pickaxe.mode.fortune.2");
        } else if (modeID == 4) {
            mode = StatCollector.translateToLocal("info.Sanguimancy.tooltip.pickaxe.mode.fortune.3");
        } else if (modeID == 5) {
            mode = StatCollector.translateToLocal("info.Sanguimancy.tooltip.pickaxe.mode.smelt");
        }
        return mode;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        EnergyItems.checkAndSetItemOwner(stack, player);
        if (player.isSneaking()) nextToolMode(stack);
        return stack;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
        RandomUtils.checkAndSetCompound(stack);
        stack.setItemDamage(0);
        super.onUpdate(stack, world, entity, par4, par5);
    }

    @Override
    public float getDigSpeed(ItemStack stack, Block block, int meta) {
        RandomUtils.checkAndSetCompound(stack);
        if (!stack.stackTagCompound.getString("ownerName").isEmpty() && SpellHelper.getPlayerForUsername(stack.stackTagCompound.getString("ownerName")) != null) {
            EntityPlayer player = SpellHelper.getPlayerForUsername(stack.stackTagCompound.getString("ownerName"));
            NBTTagCompound tag = SoulCorruptionHelper.getModTag(player, Sanguimancy.modid);
            int playerCorruption = SoulCorruptionHelper.getCorruptionLevel(player, tag);
            return super.getDigSpeed(stack, block, meta) * (playerCorruption / minimumCorruption);
        }
        return 1.0F;
    }
}