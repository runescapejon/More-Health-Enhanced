package com.nohero.morehealth.GUI;

import com.nohero.morehealth.mod_moreHealthEnhanced;
import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;
import cpw.mods.fml.client.IModGuiFactory.RuntimeOptionGuiHandler;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

public class MoreHealthGuiFactory
  implements IModGuiFactory
{
  public void initialize(Minecraft minecraftInstance) {}
  
  public Class<? extends GuiScreen> mainConfigGuiClass()
  {
    return ModConfigGUI.class;
  }
  
  public Set<IModGuiFactory.RuntimeOptionCategoryElement> runtimeGuiCategories()
  {
    return null;
  }
  
  public IModGuiFactory.RuntimeOptionGuiHandler getHandlerFor(IModGuiFactory.RuntimeOptionCategoryElement element)
  {
    return null;
  }
  
  public static class ModConfigGUI
    extends GuiConfig
  {
    public ModConfigGUI(GuiScreen parent)
    {
      super(
        getConfigElements(), "morehealth", false, false, 
        GuiConfig.getAbridgedConfigPath(mod_moreHealthEnhanced.config.toString()));
    }
    
    private static List<IConfigElement> getConfigElements()
    {
      List<IConfigElement> list = new ArrayList();
      list.addAll(new ConfigElement(mod_moreHealthEnhanced.config.getCategory("general")).getChildElements());
      list.addAll(new ConfigElement(mod_moreHealthEnhanced.config.getCategory("gui options")).getChildElements());
      return list;
    }
  }
}
