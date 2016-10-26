package com.nohero.morehealth.EventHandlers;

import com.nohero.morehealth.mod_moreHealthEnhanced;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class ForgeEventHandler
{
  @SubscribeEvent
  public void onEntityLivingDeath(LivingDeathEvent event)
  {
    if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
      if ((mod_moreHealthEnhanced.HeartContainersAndPieces) && (
        ((event.entity instanceof EntityDragon)) || ((event.entity instanceof EntityWither)))) {
        event.entity.entityDropItem(new ItemStack(mod_moreHealthEnhanced.heartContainer), 0.0F);
      }
    }
  }
}
