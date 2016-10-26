package com.nohero.morehealth.EventHandlers;

import com.nohero.morehealth.PlayerStats;
import com.nohero.morehealth.mod_moreHealthEnhanced;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerHandler
{
  public static final UUID moreHealthID = UUID.fromString("e3723b50-7cc6-11e3-baa7-0800200c9a66");
  public static ConcurrentHashMap<String, PlayerStats> playerStats = new ConcurrentHashMap();
  
  @SubscribeEvent
  public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent loginEvent)
  {
    EntityPlayer player = loginEvent.player;
    NBTTagCompound tags = player.getEntityData();
    PlayerStats stats = new PlayerStats();
    
    PlayerHandlerHelper.loadPlayerData(player, tags, stats);
    if ((stats.start != mod_moreHealthEnhanced.StartingHearts) || (!Arrays.equals(stats.LevelArray, mod_moreHealthEnhanced.LevelRampInt))) {
      PlayerHandlerHelper.updateHealth(player, stats, tags);
    }
    double healthModifier = stats.healthmod;
    addHealthModifier(player, healthModifier);
    
    stats.player = player;
    stats.justLoggedIn = true;
    playerStats.put(player.getCommandSenderName(), stats);
    
    mod_moreHealthEnhanced.updateKeyBindings();
  }
  
  public static void addHealthModifier(EntityPlayer player, double healthModifier)
  {
    mod_moreHealthEnhanced.healthMod = new AttributeModifier(moreHealthID, "More Health Heart Modifier", healthModifier, 0);
    IAttributeInstance attributeinstance = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
    attributeinstance.removeModifier(mod_moreHealthEnhanced.healthMod);
    attributeinstance.applyModifier(mod_moreHealthEnhanced.healthMod);
  }
  
  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public void onServerStop(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
  {
    Side side = FMLCommonHandler.instance().getEffectiveSide();
    if (side == Side.CLIENT)
    {
      Minecraft mc = FMLClientHandler.instance().getClient();
      EntityPlayer currentPlayer = mc.thePlayer;
      
      PlayerHandlerHelper.savePlayerData(currentPlayer, true);
    }
  }
  
  @SubscribeEvent
  public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent changedDimensionEvent)
  {
    Side side = FMLCommonHandler.instance().getEffectiveSide();
    
    EntityPlayer currentPlayer = changedDimensionEvent.player;
    PlayerHandlerHelper.savePlayerData(currentPlayer, false);
    
    PlayerHandlerHelper.updatePlayerData(currentPlayer);
    
    PlayerStats stats = PlayerStats.getPlayerStats(currentPlayer.getCommandSenderName());
    stats.needClientSideHealthUpdate = true;
  }
  
  @SubscribeEvent
  public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent respawnEvent)
  {
    EntityPlayer currentPlayer = respawnEvent.player;
    PlayerStats stats = PlayerStats.getPlayerStats(currentPlayer.getCommandSenderName());
    
    double healthModifier = stats.healthmod;
    addHealthModifier(currentPlayer, healthModifier);
    if (mod_moreHealthEnhanced.hcMode)
    {
      stats.count = 0;
      double baseHearts = currentPlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue();
      healthModifier = stats.start * 2 - baseHearts;
      addHealthModifier(currentPlayer, healthModifier);
    }
    currentPlayer.setHealth(currentPlayer.getMaxHealth());
    
    NBTTagCompound tags = currentPlayer.getEntityData();
    NBTTagCompound tagTemp = new NBTTagCompound();
    tagTemp.setInteger("count", stats.count);
    
    tagTemp.setInteger("start", stats.start);
    tagTemp.setInteger("previousLevel", stats.previousLevel);
    if (mod_moreHealthEnhanced.RpgMode) {
      tagTemp.setIntArray("LevelArray", stats.LevelArray);
    } else {
      tagTemp.setIntArray("LevelArray", new int[] { -1 });
    }
    tags.setTag("MoreHealth 1", tagTemp);
    try
    {
      stats.healthmod = currentPlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth).getModifier(moreHealthID).getAmount();
    }
    catch (Exception localException) {}
    tags.getCompoundTag("MoreHealth 1").setDouble("healthModifier", stats.healthmod);
    
    playerStats.put(currentPlayer.getCommandSenderName(), stats);
  }
}
