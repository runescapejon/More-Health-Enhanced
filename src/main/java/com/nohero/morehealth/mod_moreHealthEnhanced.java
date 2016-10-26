package com.nohero.morehealth;

import com.nohero.morehealth.Enchantments.ArmorHealthEnchantment;
import com.nohero.morehealth.EventHandlers.FMLEventHandler;
import com.nohero.morehealth.EventHandlers.ForgeEventHandler;
import com.nohero.morehealth.EventHandlers.PlayerHandler;
import com.nohero.morehealth.GUI.MoreHealthHUD;
import com.nohero.morehealth.Items.ItemHeart;
import com.nohero.morehealth.Items.ItemHeartPiece;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.PrintStream;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

@Mod(modid="morehealth", name="More Health Forge", version="6.5 BETA", guiFactory="com.nohero.morehealth.GUI.MoreHealthGuiFactory")
public class mod_moreHealthEnhanced
{
  public static final String modid = "morehealth";
  public static final String name = "More Health Forge";
  public static final String version = "6.5 BETA";
  public static final String guiOptions = "gui options";
  public static int[] LevelRampInt;
  public static int StartingHearts = 10;
  public static int MaxHearts = -1;
  public static String LevelRamp = "1,5,10,15,20,25,30,34,38,42,46,50,53,56,59,62,64,66,68,70,72,74,76,78,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,102,104,106,108,110,120,130,140,150,160,170,180,190,200,220,240,260,280,300,320,340,360,380,400,500,600,700,800,1000,1500,2000,2500,3000,9001";
  public static boolean HeartContainersAndPieces = true;
  public static boolean RpgMode = true;
  public static boolean Enchantments = true;
  public static Item heartContainer;
  private static Item heartPiece;
  private static double multiply = 1.0D;
  public static boolean hcMode = false;
  public static boolean renderCustomGUI = true;
  public static boolean minimalisticGUI = false;
  private static Property startHearts;
  private static Property maxHearts;
  private static Property levelRamp;
  private static Property heartItems;
  private static Property rpg;
  private static Property multiplier;
  private static Property hardcore;
  private static Property enchantment;
  private static Property customGui;
  private static Property minimalGui;
  private static Property armorEnchantmentID;
  private static Property guiKeyBinding;
  public static AttributeModifier healthMod;
  public static int heartArmorEnchantID = 120;
  public static Enchantment armorEnchantment = null;
  @Mod.Instance("morehealth")
  public static mod_moreHealthEnhanced instance;
  public static Configuration config;
  private boolean guiOpened = false;
  public static PlayerHandler playerTracker;
  
  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent event)
  {
    config = new Configuration(event.getSuggestedConfigurationFile());
    
    config.load();
    updateConfig();
  }
  
  private void updateConfig()
  {
    multiplier = config.get("general", "Heart Item Multiplier", 1.0D);
    multiplier.comment = "This is the multiplier for chest heart item loot. Really useful to change on large servers. IF POSSIBLE, CHANGE BEFORE GENERATING WORLD. Multiplier changes only affects newly generated areas.";
    
    startHearts = config.get("general", "Starting Hearts", 10);
    startHearts.comment = "The hearts you start with in all your worlds. Default 10.";
    
    maxHearts = config.get("general", "Max Hearts", -1);
    maxHearts.comment = "The cap amount of hearts. Default (-1 or 0) means no cap.";
    
    levelRamp = config.get("general", "Level Ramp", "1,5,10,15,20,25,30,34,38,42,46,50,53,56,59,62,64,66,68,70,75,80,85,90,95,100,110,120,130,140,150,160,170,180,190,200,210,220,230,240,250,260,270,280,290,300,310,320,330,340,350,360,370,380,390,400,420,440,460,500");
    
    levelRamp.comment = "The levels where you can the heart. Fully customizable in the fields below.";
    
    heartItems = config.get("general", "Heart Container and Pieces", true);
    heartItems.comment = "in the field below, type true to enable heart items and type false to disable them. Default on.";
    
    rpg = config.get("general", "RPG Mode", true);
    rpg.comment = "in the field below, type true to enable rpg mode and type false to disable them. Default on.";
    
    hardcore = config.get("general", "Hardcore Mode", false);
    hardcore.comment = "Set to true to enable hardcore mode. After death, you restart back at your starting hearts value.";
    
    enchantment = config.get("general", "Enchantments", true);
    enchantment.comment = "Set false to remove the heart enchantment for armors";
    
    customGui = config.get("gui options", "More Health HUD", true);
    customGui.comment = "By default, more health will customize the HUD so that heart rows are possible. Set this to false AND set minimal HUD to false if it is conflicting with one of your HUD/GUI mods that have their own heart HUD.";
    
    minimalGui = config.get("gui options", "Minimal HUD", true);
    minimalGui.comment = "Set to true to enable minimal gui. Displays heart information in one row. A number should appear next to your hearts telling you what row you are on. Row 1= Hearts 1-10. Row 2=Hearts 11-20. Turn this on if there is a conflict with other HUD/GUI mods that DO NOT have their own heart HUD";
    
    armorEnchantmentID = config.get("general", "Armor Enchantments ID", 120);
    armorEnchantmentID.comment = "Adjust the Armor Enchants ID in case of a conflict with other custom enchantments";
    
    guiKeyBinding = config.get("general", "More Health Stats Key", "H");
    guiKeyBinding.comment = "Set the key you want to use to open up the gui with More Health stats. Supports alphanumeric. WARNING! Will unbind if key was used before!";
    if (config.hasChanged()) {
      config.save();
    }
  }
  
  @Mod.EventHandler
  public void load(FMLInitializationEvent event)
  {
    String keyName = guiKeyBinding.getString().toUpperCase();
    if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
    {
      Minecraft mc = FMLClientHandler.instance().getClient();
      MinecraftForge.EVENT_BUS.register(new MoreHealthHUD(mc));
    }
    MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
    FMLCommonHandler.instance().bus().register(new FMLEventHandler());
    FMLCommonHandler.instance().bus().register(instance);
    
    setUpValuesFromProperties();
    if (minimalisticGUI) {
      renderCustomGUI = true;
    }
    if (!RpgMode)
    {
      LevelRampInt = new int[1];
      LevelRampInt[0] = -1;
    }
    else
    {
      try
      {
        LevelRampInt = convertedStringArray(LevelRamp.split(","));
      }
      catch (Exception e)
      {
        System.out.println("There is a error in your config file. Make sure there is no extra ',' in the line for Ramp. ");
      }
    }
    if (HeartContainersAndPieces)
    {
      heartContainer = new ItemHeart();
      heartPiece = new ItemHeartPiece();
      
      GameRegistry.registerItem(heartContainer, "heartContainer");
      GameRegistry.registerItem(heartPiece, "heartPiece");
      
      GameRegistry.addRecipe(new ItemStack(heartContainer, 1), new Object[] { "XX", "XX", 
        Character.valueOf('X'), heartPiece });
      
      addChestLoot();
    }
    if (Enchantments) {
      armorEnchantment = new ArmorHealthEnchantment(heartArmorEnchantID, 4);
    }
  }
  
  @SubscribeEvent
  public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs)
  {
    if (eventArgs.modID.equals("morehealth")) {
      updateConfig();
    }
    setUpValuesFromProperties();
  }
  
  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onKeyEvent(InputEvent.KeyInputEvent event)
  {
    if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT) {
      return;
    }
    Minecraft mc = FMLClientHandler.instance().getClient();
    EntityPlayer player = mc.thePlayer;
  }
  
  private void setUpValuesFromProperties()
  {
    if (maxHearts.getInt() == 0) {
      maxHearts.set("-1");
    }
    StartingHearts = startHearts.getInt();
    MaxHearts = maxHearts.getInt();
    LevelRamp = levelRamp.getString();
    HeartContainersAndPieces = heartItems.getBoolean(true);
    RpgMode = rpg.getBoolean(true);
    multiply = multiplier.getDouble(1.0D);
    hcMode = hardcore.getBoolean(false);
    Enchantments = enchantment.getBoolean(true);
    renderCustomGUI = customGui.getBoolean(true);
    minimalisticGUI = minimalGui.getBoolean(true);
    heartArmorEnchantID = armorEnchantmentID.getInt();
  }
  
  private void addChestLoot()
  {
    ChestGenHooks dungeon = ChestGenHooks.getInfo("dungeonChest");
    dungeon.addItem(new WeightedRandomChestContent(new ItemStack(heartContainer), 1, 1, (int)(6.0D * multiply)));
    dungeon.addItem(new WeightedRandomChestContent(new ItemStack(heartPiece), 1, 3, (int)(8.0D * multiply)));
    
    ChestGenHooks desert = ChestGenHooks.getInfo("pyramidDesertyChest");
    desert.addItem(new WeightedRandomChestContent(new ItemStack(heartPiece), 2, 3, (int)(9.0D * multiply)));
    desert.addItem(new WeightedRandomChestContent(new ItemStack(heartContainer), 1, 1, (int)(5.0D * multiply)));
    
    ChestGenHooks jungle = ChestGenHooks.getInfo("pyramidJungleChest");
    jungle.addItem(new WeightedRandomChestContent(new ItemStack(heartPiece), 2, 3, (int)(8.0D * multiply)));
    jungle.addItem(new WeightedRandomChestContent(new ItemStack(heartContainer), 1, 1, (int)(5.0D * multiply)));
    
    ChestGenHooks library = ChestGenHooks.getInfo("strongholdLibrary");
    library.addItem(new WeightedRandomChestContent(new ItemStack(heartPiece), 1, 2, (int)(5.0D * multiply)));
    
    ChestGenHooks corridor = ChestGenHooks.getInfo("strongholdCorridor");
    corridor.addItem(new WeightedRandomChestContent(new ItemStack(heartPiece), 1, 3, (int)(8.0D * multiply)));
    corridor.addItem(new WeightedRandomChestContent(new ItemStack(heartContainer), 1, 1, (int)(6.0D * multiply)));
    
    ChestGenHooks blacksmith = ChestGenHooks.getInfo("villageBlacksmith");
    blacksmith.addItem(new WeightedRandomChestContent(new ItemStack(heartContainer), 1, 1, (int)(8.0D * multiply)));
    
    ChestGenHooks crossing = ChestGenHooks.getInfo("strongholdCrossing");
    crossing.addItem(new WeightedRandomChestContent(new ItemStack(heartPiece), 1, 2, (int)(7.0D * multiply)));
    
    ChestGenHooks mineshaft = ChestGenHooks.getInfo("mineshaftCorridor");
    mineshaft.addItem(new WeightedRandomChestContent(new ItemStack(heartPiece), 1, 2, (int)(7.0D * multiply)));
  }
  
  @Mod.EventHandler
  public void postInit(FMLPostInitializationEvent evt)
  {
    playerTracker = new PlayerHandler();
    FMLCommonHandler.instance().bus().register(playerTracker);
    MinecraftForge.EVENT_BUS.register(playerTracker);
  }
  
  public int[] convertedStringArray(String[] sarray)
    throws Exception
  {
    if (sarray != null)
    {
      int[] intarray = new int[sarray.length];
      for (int i = 0; i < sarray.length; i++) {
        intarray[i] = Integer.parseInt(sarray[i]);
      }
      return intarray;
    }
    return null;
  }
  
  public static void updateKeyBindings()
  {
    if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT) {}
  }
}
