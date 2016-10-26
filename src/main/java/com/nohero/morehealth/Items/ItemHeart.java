package com.nohero.morehealth.Items;

import com.nohero.morehealth.EventHandlers.PlayerHandler;
import com.nohero.morehealth.PlayerStats;
import com.nohero.morehealth.mod_moreHealthEnhanced;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class ItemHeart
  extends Item
{
  public ItemHeart()
  {
    setUnlocalizedName("morehealth_heartContainer");
    setTextureName("morehealth:heartContainer");
    setCreativeTab(CreativeTabs.tabMisc);
    
    this.maxStackSize = 64;
  }
  
  public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer)
  {
    Side side = FMLCommonHandler.instance().getEffectiveSide();
    if (side.isClient()) {
      return itemstack;
    }
    if (!mod_moreHealthEnhanced.HeartContainersAndPieces)
    {
      entityplayer.addChatComponentMessage(new ChatComponentText("Can't use, please turn heart containers on in the config!"));
      return itemstack;
    }
    itemstack.stackSize -= 1;
    if ((mod_moreHealthEnhanced.MaxHearts != -1) && (mod_moreHealthEnhanced.MaxHearts != 0)) {
      if (entityplayer.getMaxHealth() + 2.0F > mod_moreHealthEnhanced.MaxHearts * 2)
      {
        entityplayer.addChatComponentMessage(new ChatComponentText("Your Life is fully replenished!"));
        entityplayer.setHealth(entityplayer.getMaxHealth());
        return itemstack;
      }
    }
    double updatedModifier = 2.0D;
    try
    {
      updatedModifier = entityplayer.getEntityAttribute(SharedMonsterAttributes.maxHealth).getModifier(PlayerHandler.moreHealthID).getAmount() + 2.0D;
    }
    catch (Exception localException) {}
    PlayerHandler.addHealthModifier(entityplayer, updatedModifier);
    
    entityplayer.addChatComponentMessage(new ChatComponentText("Your Life has increased by one and is also now fully replenished!"));
    PlayerStats stats = PlayerStats.getPlayerStats(entityplayer.getCommandSenderName());
    stats.healthmod = updatedModifier;
    stats.heartContainers += 1;
    entityplayer.setHealth(entityplayer.getMaxHealth());
    return itemstack;
  }
}
