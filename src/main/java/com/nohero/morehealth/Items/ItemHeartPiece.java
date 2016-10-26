package com.nohero.morehealth.Items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemHeartPiece
  extends Item
{
  public ItemHeartPiece()
  {
    setCreativeTab(CreativeTabs.tabMisc);
    setUnlocalizedName("morehealth_heartPiece");
    setTextureName("morehealth:heartPiece");
  }
}
