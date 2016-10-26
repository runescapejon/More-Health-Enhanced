package com.nohero.morehealth.Enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.util.StatCollector;

public class ArmorHealthEnchantment
  extends Enchantment
{
  public ArmorHealthEnchantment(int effectID, int weight)
  {
    super(effectID, weight, EnumEnchantmentType.armor);
    setName("Hearts");
  }
  
  public int getMinEnchantability(int enchantLevel)
  {
    return 5 + 5 * enchantLevel;
  }
  
  public int getMaxEnchantability(int enchantLevel)
  {
    return getMinEnchantability(enchantLevel) + 50;
  }
  
  public int getMaxLevel()
  {
    return 5;
  }
  
  public String getTranslatedName(int p_77316_1_)
  {
    String s = "Hearts";
    return s + " " + StatCollector.translateToLocal(new StringBuilder().append("enchantment.level.").append(p_77316_1_).toString());
  }
}
