package com.nohero.morehealth.EventHandlers;

import java.util.concurrent.*;
import cpw.mods.fml.common.gameevent.*;
import com.nohero.morehealth.*;
import java.util.*;
import net.minecraft.entity.player.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.*;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.client.*;
import net.minecraft.client.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.nbt.*;

public class PlayerHandler
{
    public static final UUID moreHealthID;
    public static ConcurrentHashMap<String, PlayerStats> playerStats;
    
    @SubscribeEvent
    public void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent loginEvent) {
        final EntityPlayer player = loginEvent.player;
        final NBTTagCompound tags = player.getEntityData();
        final PlayerStats stats = new PlayerStats();
        PlayerHandlerHelper.loadPlayerData(player, tags, stats);
        if (stats.start != mod_moreHealthEnhanced.StartingHearts || !Arrays.equals(stats.LevelArray, mod_moreHealthEnhanced.LevelRampInt)) {
            PlayerHandlerHelper.updateHealth(player, stats, tags);
        }
        final double healthModifier = stats.healthmod;
        addHealthModifier(player, healthModifier);
        stats.player = player;
        stats.justLoggedIn = true;
        PlayerHandler.playerStats.put(player.getCommandSenderName(), stats);
        mod_moreHealthEnhanced.updateKeyBindings();
    }
    
    public static void addHealthModifier(final EntityPlayer player, final double healthModifier) {
        mod_moreHealthEnhanced.healthMod = new AttributeModifier(PlayerHandler.moreHealthID, "More Health Heart Modifier", healthModifier, 0);
        final IAttributeInstance attributeinstance = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        attributeinstance.removeModifier(mod_moreHealthEnhanced.healthMod);
        attributeinstance.applyModifier(mod_moreHealthEnhanced.healthMod);
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onServerStop(final FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        final Side side = FMLCommonHandler.instance().getEffectiveSide();
        if (side == Side.CLIENT) {
            final Minecraft mc = FMLClientHandler.instance().getClient();
            final EntityPlayer currentPlayer = (EntityPlayer)mc.thePlayer;
            PlayerHandlerHelper.savePlayerData(currentPlayer, true);
        }
    }
    
    @SubscribeEvent
    public void onPlayerChangedDimension(final PlayerEvent.PlayerChangedDimensionEvent changedDimensionEvent) {
        final Side side = FMLCommonHandler.instance().getEffectiveSide();
        final EntityPlayer currentPlayer = changedDimensionEvent.player;
        PlayerHandlerHelper.savePlayerData(currentPlayer, false);
        PlayerHandlerHelper.updatePlayerData(currentPlayer);
        final PlayerStats stats = PlayerStats.getPlayerStats(currentPlayer.getCommandSenderName());
        stats.needClientSideHealthUpdate = true;
    }
    
    @SubscribeEvent
    public void onPlayerRespawn(final PlayerEvent.PlayerRespawnEvent respawnEvent) {
        final EntityPlayer currentPlayer = respawnEvent.player;
        final PlayerStats stats = PlayerStats.getPlayerStats(currentPlayer.getCommandSenderName());
        double healthModifier = stats.healthmod;
        addHealthModifier(currentPlayer, healthModifier);
        if (mod_moreHealthEnhanced.hcMode) {
            stats.count = 0;
            final double baseHearts = currentPlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue();
            healthModifier = stats.start * 2 - baseHearts;
            addHealthModifier(currentPlayer, healthModifier);
        }
        currentPlayer.setHealth(currentPlayer.getMaxHealth());
        final NBTTagCompound tags = currentPlayer.getEntityData();
        final NBTTagCompound tagTemp = new NBTTagCompound();
        tagTemp.setInteger("count", stats.count);
        tagTemp.setInteger("start", stats.start);
        tagTemp.setInteger("previousLevel", stats.previousLevel);
        if (mod_moreHealthEnhanced.RpgMode) {
            tagTemp.setIntArray("LevelArray", stats.LevelArray);
        }
        else {
            tagTemp.setIntArray("LevelArray", new int[] { -1 });
        }
        tags.setTag("MoreHealth 1", (NBTBase)tagTemp);
        try {
            stats.healthmod = currentPlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth).getModifier(PlayerHandler.moreHealthID).getAmount();
        }
        catch (Exception ex) {}
        tags.getCompoundTag("MoreHealth 1").setDouble("healthModifier", stats.healthmod);
        PlayerHandler.playerStats.put(currentPlayer.getCommandSenderName(), stats);
    }
    
    static {
        moreHealthID = UUID.fromString("e3723b50-7cc6-11e3-baa7-0800200c9a66");
        PlayerHandler.playerStats = new ConcurrentHashMap<String, PlayerStats>();
    }
}
