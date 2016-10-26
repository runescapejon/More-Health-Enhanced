package com.nohero.morehealth;

import com.nohero.morehealth.EventHandlers.PlayerHandler;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PlayerStats
{
  public int[] LevelArray;
  public int start;
  public int hpmax;
  public int count;
  public int previousLevel;
  public double healthmod;
  public EntityPlayer player;
  public boolean needClientSideHealthUpdate = false;
  public ItemStack[] oldArmorSet = new ItemStack[4];
  public boolean justLoggedIn;
  public float loggedOutHealth;
  public int heartContainers;
  
  public static PlayerStats getPlayerStats(String username)
  {
    PlayerStats stats = (PlayerStats)PlayerHandler.playerStats.get(username);
    if (stats == null)
    {
      stats = new PlayerStats();
      
      PlayerHandler.playerStats.put(username, stats);
    }
    return stats;
  }
}
