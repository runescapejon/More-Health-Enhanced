package com.nohero.morehealth.EventHandlers;

import com.nohero.morehealth.PlayerStats;
import com.nohero.morehealth.mod_moreHealthEnhanced;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

public class FMLEventHandler
{
  @SubscribeEvent
  public void onPlayerLivingUpdate(TickEvent.PlayerTickEvent event)
  {
    EntityPlayer player = event.player;
    PlayerStats stats = PlayerStats.getPlayerStats(player.getCommandSenderName());
    
    Side side = FMLCommonHandler.instance().getEffectiveSide();
    side = event.side;
    if ((side == Side.CLIENT) && (stats.needClientSideHealthUpdate))
    {
      PlayerHandlerHelper.savePlayerData(player, false);
      PlayerHandlerHelper.updatePlayerData(player);
      player.setHealth(player.getHealth());
      stats.needClientSideHealthUpdate = false;
      if ((stats.justLoggedIn) && (stats.loggedOutHealth != 0.0F))
      {
        player.setHealth(stats.loggedOutHealth);
        stats.justLoggedIn = false;
      }
    }
    if (mod_moreHealthEnhanced.RpgMode) {
      calculateHeartChange(player, stats);
    }
    if ((side == Side.SERVER) && (mod_moreHealthEnhanced.Enchantments)) {
      calculateEnchantmentChanges(player, stats);
    }
    saveHeartChange(player, stats);
  }
  
  private void calculateEnchantmentChanges(EntityPlayer player, PlayerStats stats)
  {
    if ((!stats.needClientSideHealthUpdate) || 
    
      (stats.loggedOutHealth == 0.0F)) {
      return;
    }
    int armorHealth = 0;
    for (int i = 1; i <= 4; i++)
    {
      ItemStack currentArmor = player.getEquipmentInSlot(i);
      ItemStack oldArmor = stats.oldArmorSet[(i - 1)];
      
      double currentMaxHealthMod = 0.0D;
      try
      {
        currentMaxHealthMod = player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getModifier(PlayerHandler.moreHealthID).getAmount();
      }
      catch (Exception e)
      {
        return;
      }
      if (oldArmor != currentArmor) {
        if ((currentArmor == null) && (oldArmor != null))
        {
          int extraHearts = EnchantmentHelper.getEnchantmentLevel(mod_moreHealthEnhanced.heartArmorEnchantID, oldArmor);
          if (extraHearts > 0)
          {
            int extraHealth = extraHearts * 2;
            
            PlayerHandler.addHealthModifier(player, currentMaxHealthMod - extraHealth);
            player.addChatComponentMessage(new ChatComponentText("Removing the armor causes the extra " + extraHearts + " enchanted hearts to fade away."));
            
            stats.needClientSideHealthUpdate = true;
          }
        }
        else if ((oldArmor == null) && (currentArmor != null))
        {
          int extraHearts = EnchantmentHelper.getEnchantmentLevel(mod_moreHealthEnhanced.heartArmorEnchantID, currentArmor);
          if (extraHearts > 0)
          {
            int extraHealth = extraHearts * 2;
            PlayerHandler.addHealthModifier(player, currentMaxHealthMod + extraHealth);
            if (!stats.justLoggedIn) {
              player.addChatComponentMessage(new ChatComponentText("Equipping the armor binds an extra " + extraHearts + " enchanted hearts to your soul."));
            }
            stats.needClientSideHealthUpdate = true;
            armorHealth += extraHealth;
          }
        }
        else
        {
          int oldHealth = 2 * EnchantmentHelper.getEnchantmentLevel(mod_moreHealthEnhanced.heartArmorEnchantID, oldArmor);
          int newHealth = 2 * EnchantmentHelper.getEnchantmentLevel(mod_moreHealthEnhanced.heartArmorEnchantID, currentArmor);
          int healthChange = newHealth - oldHealth;
          PlayerHandler.addHealthModifier(player, currentMaxHealthMod + healthChange);
          if (healthChange > 0)
          {
            player.addChatComponentMessage(new ChatComponentText("Equipping the stronger new armor binds an extra " + healthChange + " enchanted hearts to your soul."));
            stats.needClientSideHealthUpdate = true;
          }
          if (healthChange < 0)
          {
            player.addChatComponentMessage(new ChatComponentText("Equipping the weaker new armor releases an extra " + healthChange + " enchanted hearts."));
            stats.needClientSideHealthUpdate = true;
          }
        }
      }
      stats.oldArmorSet[(i - 1)] = currentArmor;
    }
    if (player.getHealth() > player.getMaxHealth()) {
      player.setHealth(player.getMaxHealth());
    }
    if (stats.justLoggedIn)
    {
      player.setHealth(stats.loggedOutHealth);
      stats.needClientSideHealthUpdate = true;
    }
  }
  
  private void calculateHeartChange(EntityPlayer player, PlayerStats stats)
  {
    if ((mod_moreHealthEnhanced.MaxHearts != -1) && (mod_moreHealthEnhanced.MaxHearts != 0)) {
      if (player.getMaxHealth() + 2.0F > mod_moreHealthEnhanced.MaxHearts * 2) {
        return;
      }
    }
    if (levelIncreased(player, stats)) {
      while ((stats.count < mod_moreHealthEnhanced.LevelRampInt.length) && (player.experienceLevel >= mod_moreHealthEnhanced.LevelRampInt[stats.count]))
      {
        player.addChatComponentMessage(new ChatComponentText("Your Life has increased by one and is also now fully replenished!"));
        double updatedModifier = 0.0D;
        try
        {
          updatedModifier = player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getModifier(PlayerHandler.moreHealthID).getAmount() + 2.0D;
        }
        catch (Exception localException) {}
        PlayerHandler.addHealthModifier(player, updatedModifier);
        player.setHealth(player.getMaxHealth());
        stats.count += 1;
      }
    }
  }
  
  private void saveHeartChange(EntityPlayer player, PlayerStats stats)
  {
    NBTTagCompound tags = player.getEntityData();
    tags.getCompoundTag("MoreHealth 1").setInteger("count", stats.count);
    try
    {
      stats.healthmod = player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getModifier(PlayerHandler.moreHealthID).getAmount();
    }
    catch (Exception localException) {}
    tags.getCompoundTag("MoreHealth 1").setDouble("healthModifier", stats.healthmod);
    tags.getCompoundTag("MoreHealth 1").setInteger("previousLevel", stats.previousLevel);
    PlayerHandler.playerStats.put(player.getCommandSenderName(), stats);
  }
  
  private boolean levelIncreased(EntityPlayer player, PlayerStats stats)
  {
    boolean levelIncreased = false;
    if (stats.previousLevel != player.experienceLevel)
    {
      stats.previousLevel = player.experienceLevel;
      levelIncreased = true;
    }
    return levelIncreased;
  }
}
