package com.nohero.morehealth.GUI;

import cpw.mods.fml.client.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import com.nohero.morehealth.*;
import cpw.mods.fml.client.config.*;
import net.minecraftforge.common.config.*;
import java.util.*;

public class MoreHealthGuiFactory implements IModGuiFactory
{
    public void initialize(final Minecraft minecraftInstance) {
    }
    
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return (Class<? extends GuiScreen>)ModConfigGUI.class;
    }
    
    public Set<IModGuiFactory.RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
    
    public IModGuiFactory.RuntimeOptionGuiHandler getHandlerFor(final IModGuiFactory.RuntimeOptionCategoryElement element) {
        return null;
    }
    
    public static class ModConfigGUI extends GuiConfig
    {
        public ModConfigGUI(final GuiScreen parent) {
            super(parent, (List)getConfigElements(), "morehealth", false, false, GuiConfig.getAbridgedConfigPath(mod_moreHealthEnhanced.config.toString()));
        }
        
        private static List<IConfigElement> getConfigElements() {
            final List<IConfigElement> list = new ArrayList<IConfigElement>();
            list.addAll(new ConfigElement(mod_moreHealthEnhanced.config.getCategory("general")).getChildElements());
            list.addAll(new ConfigElement(mod_moreHealthEnhanced.config.getCategory("gui options")).getChildElements());
            return list;
        }
    }
}
