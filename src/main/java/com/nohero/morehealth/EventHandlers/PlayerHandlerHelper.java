package com.nohero.morehealth.EventHandlers;

import com.nohero.morehealth.PlayerStats;
import com.nohero.morehealth.mod_moreHealthEnhanced;
import java.util.Arrays;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

public class PlayerHandlerHelper {
	public static void updateHealth(EntityPlayer player, PlayerStats stats, NBTTagCompound tags) {
		NBTTagCompound moreHealthTag = (NBTTagCompound) tags.getTag("MoreHealth 1");
		if (stats.start != mod_moreHealthEnhanced.StartingHearts) {
			stats.start = mod_moreHealthEnhanced.StartingHearts;
			player.addChatComponentMessage(new ChatComponentText("Starting Hearts successfully changed!"));
			moreHealthTag.setInteger("start", stats.start);
		}
		if (!Arrays.equals(stats.LevelArray, mod_moreHealthEnhanced.LevelRampInt)) {
			stats.LevelArray = mod_moreHealthEnhanced.LevelRampInt;
			player.addChatComponentMessage(new ChatComponentText("Level Ramp successfully changed!"));
			moreHealthTag.setIntArray("LevelArray", stats.LevelArray);
		}
		stats.hpmax = (stats.start * 2);
		if ((mod_moreHealthEnhanced.MaxHearts != -1) && (mod_moreHealthEnhanced.MaxHearts != 0)) {
			if (stats.hpmax > mod_moreHealthEnhanced.MaxHearts * 2) {
				stats.hpmax = (mod_moreHealthEnhanced.MaxHearts * 2);
			}
		}
		moreHealthTag.setInteger("hpmax", stats.hpmax);

		player.setHealth(player.getMaxHealth());
		if (!mod_moreHealthEnhanced.RpgMode) {
			player.addChatComponentMessage(new ChatComponentText("Heart Container mode enable!"));
			return;
		}
		stats.hpmax = (stats.start * 2);
		moreHealthTag.setInteger("hpmax", stats.hpmax);

		double newMax = calculateTotalMoreHealthContribution(player, stats);

		double healthModifier = newMax - 20.0D;

		PlayerHandler.addHealthModifier(player, healthModifier);

		player.setHealth(player.getMaxHealth());
		if (stats.count > 0) {
			player.addChatComponentMessage(
					new ChatComponentText("Your Life has increased and is also now fully replenished!"));
		}
		if ((mod_moreHealthEnhanced.RpgMode == true) && (mod_moreHealthEnhanced.HeartContainersAndPieces == true)) {
			player.addChatComponentMessage(new ChatComponentText("Enhanced mode activated! (RPG + Heart Containers)"));
		} else if (mod_moreHealthEnhanced.RpgMode == true) {
			player.addChatComponentMessage(new ChatComponentText("RPG mode enabled!"));
		}
		if (stats.start == 3) {
			player.addChatComponentMessage(new ChatComponentText("Legend of Zelda <3"));
		}
		moreHealthTag.setInteger("count", stats.count);
		try {
			stats.healthmod = player.getEntityAttribute(SharedMonsterAttributes.maxHealth)
					.getModifier(PlayerHandler.moreHealthID).getAmount();
		} catch (Exception localException) {
		}
		moreHealthTag.setDouble("healthModifier", stats.healthmod);
	}

	public static double calculateTotalMoreHealthContribution(EntityPlayer player, PlayerStats stats) {
		int rpgHealth = 0;
		for (int i = 0; i < mod_moreHealthEnhanced.LevelRampInt.length; i++) {
			if (player.experienceLevel < mod_moreHealthEnhanced.LevelRampInt[i]) {
				break;
			}
			rpgHealth += 2;
		}
		if ((mod_moreHealthEnhanced.MaxHearts != -1) && (mod_moreHealthEnhanced.MaxHearts != 0)) {
			if (rpgHealth > mod_moreHealthEnhanced.MaxHearts * 2) {
				rpgHealth = mod_moreHealthEnhanced.MaxHearts * 2;
			}
		}
		int extraHearts = 0;
		for (int i = 0; i < stats.oldArmorSet.length; i++) {
			extraHearts += EnchantmentHelper.getEnchantmentLevel(mod_moreHealthEnhanced.heartArmorEnchantID,
					stats.oldArmorSet[i]);
		}
		double armorHealth = extraHearts * 2;
		double heartContainerHealth = stats.heartContainers * 2;
		return stats.start * 2 + rpgHealth + armorHealth + heartContainerHealth;
	}

	static void setupFirstTime(EntityPlayer player, NBTTagCompound tags, PlayerStats stats) {
		stats.start = mod_moreHealthEnhanced.StartingHearts;
		stats.LevelArray = mod_moreHealthEnhanced.LevelRampInt;

		double healthModifier = stats.start * 2 - 20;

		PlayerHandler.addHealthModifier(player, healthModifier);

		stats.count = 0;
		stats.previousLevel = player.experienceLevel;
		try {
			stats.healthmod = player.getEntityAttribute(SharedMonsterAttributes.maxHealth)
					.getModifier(PlayerHandler.moreHealthID).getAmount();
		} catch (Exception localException) {
		}
		NBTTagCompound moreHealthTag = (NBTTagCompound) tags.getTag("MoreHealth 1");
		moreHealthTag.setInteger("start", stats.start);
		if (mod_moreHealthEnhanced.RpgMode) {
			moreHealthTag.setIntArray("LevelArray", stats.LevelArray);
		} else {
			moreHealthTag.setIntArray("LevelArray", new int[] { -1 });
		}
		moreHealthTag.setInteger("count", stats.count);
		moreHealthTag.setInteger("previousLevel", stats.previousLevel);
		moreHealthTag.setDouble("healthModifier", stats.healthmod);
		moreHealthTag.setInteger("heartContainers", stats.heartContainers);

		updateHealth(player, stats, tags);
	}

	static void updatePlayerData(EntityPlayer player) {
		PlayerStats stats = PlayerStats.getPlayerStats(player.getCommandSenderName());

		double healthModifier = stats.healthmod;
		PlayerHandler.addHealthModifier(player, healthModifier);
	}

	public static void savePlayerData(EntityPlayer player, boolean loggedOut) {
		PlayerStats stats = PlayerStats.getPlayerStats(player.getCommandSenderName());
		if (stats != null) {
			EntityPlayer realPlayer = stats.player;
			NBTTagCompound entityPlayerTag;
			NBTTagCompound entityPlayerTag1;
			if (realPlayer != null) {
				entityPlayerTag1 = realPlayer.getEntityData();
			} else {
				entityPlayerTag1 = player.getEntityData();
			}
			NBTTagCompound moreHealthTag = (NBTTagCompound) entityPlayerTag1.getTag("MoreHealth 1");

			moreHealthTag.setInteger("start", stats.start);
			if (mod_moreHealthEnhanced.RpgMode) {
				moreHealthTag.setIntArray("LevelArray", stats.LevelArray);
			} else {
				moreHealthTag.setIntArray("LevelArray", new int[] { -1 });
			}
			moreHealthTag.setInteger("count", stats.count);
			moreHealthTag.setInteger("previousLevel", stats.previousLevel);

			moreHealthTag.setDouble("healthModifier", stats.healthmod);
			if (loggedOut) {
				double currentMaxHealthMod = 0.0D;
				try {
					currentMaxHealthMod = player.getEntityAttribute(SharedMonsterAttributes.maxHealth)
							.getModifier(PlayerHandler.moreHealthID).getAmount();
				} catch (Exception e) {
					return;
				}
				for (int i = 0; i < 4; i++) {
					ItemStack currArmor = stats.oldArmorSet[i];
					int extraHearts = EnchantmentHelper.getEnchantmentLevel(mod_moreHealthEnhanced.heartArmorEnchantID,
							currArmor);
					if (extraHearts > 0) {
						int extraHealth = extraHearts * 2;

						currentMaxHealthMod -= extraHealth;
					}
				}
				moreHealthTag.setDouble("healthModifier", currentMaxHealthMod);
				moreHealthTag.setFloat("loggedOutHealth", player.getHealth());
				moreHealthTag.setInteger("heartContainers", stats.heartContainers);
				PlayerHandler.playerStats.remove(player.getCommandSenderName());
			}
		}
	}

	static void loadPlayerData(EntityPlayer player, NBTTagCompound tags, PlayerStats stats) {
		if (!tags.hasKey("MoreHealth 1")) {
			tags.setTag("MoreHealth 1", new NBTTagCompound());
			NBTTagCompound temp = (NBTTagCompound) tags.getTag("MoreHealth 1");
			if (temp == null) {
			}
			setupFirstTime(player, tags, stats);
		}
		NBTTagCompound moreHealthTag = (NBTTagCompound) tags.getTag("MoreHealth 1");

		stats.start = moreHealthTag.getInteger("start");
		stats.LevelArray = moreHealthTag.getIntArray("LevelArray");

		stats.count = moreHealthTag.getInteger("count");
		stats.previousLevel = moreHealthTag.getInteger("previousLevel");
		stats.healthmod = moreHealthTag.getDouble("healthModifier");
		stats.loggedOutHealth = moreHealthTag.getFloat("loggedOutHealth");

		stats.heartContainers = moreHealthTag.getInteger("heartContainers");
	}
}
