package com.nohero.morehealth.GUI;

import com.nohero.morehealth.mod_moreHealthEnhanced;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeHooks;
import org.lwjgl.opengl.GL11;

public class MoreHealthHUD extends Gui {
	private Minecraft mc;
	protected final Random rand = new Random();

	public MoreHealthHUD(Minecraft mc) {
		this.mc = Minecraft.getMinecraft();
	}

	private void bind(ResourceLocation res) {
		this.mc.getTextureManager().bindTexture(res);
	}

	@SubscribeEvent
	public void modifyAirHUD(RenderGameOverlayEvent.Pre event) {
		if (event == null) {
			return;
		}
		if (event.type == null) {
			return;
		}
		if (event.type.equals(RenderGameOverlayEvent.ElementType.AIR)) {
			if (mod_moreHealthEnhanced.renderCustomGUI) {
				if (!mod_moreHealthEnhanced.minimalisticGUI) {
					event.setCanceled(true);
					this.mc.mcProfiler.startSection("air");

					ScaledResolution res = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
					int width = res.getScaledWidth();
					int height = res.getScaledHeight();

					int left = width / 2 + 91;
					int top = height - 49;
					if (this.mc.thePlayer.isInsideOfMaterial(Material.water)) {
						int level = ForgeHooks.getTotalArmorValue(this.mc.thePlayer);
						if (level > 0) {
							top -= 9;
						}
						int air = this.mc.thePlayer.getAir();
						int full = MathHelper.ceiling_double_int((air - 2) * 10.0D / 300.0D);
						int partial = MathHelper.ceiling_double_int(air * 10.0D / 300.0D) - full;
						for (int i = 0; i < full + partial; i++) {
							drawTexturedModalRect(left - i * 8 - 9, top, i < full ? 16 : 25, 18, 9, 9);
						}
					}
					this.mc.mcProfiler.endSection();
				}
			}
		}
	}

	@SubscribeEvent
	public void modifyArmorHUD(RenderGameOverlayEvent.Pre event) {
		if (event == null) {
			return;
		}
		if (event.type == null) {
			return;
		}
		if (event.type.equals(RenderGameOverlayEvent.ElementType.ARMOR)) {
			if (mod_moreHealthEnhanced.minimalisticGUI) {
				GuiIngameForge.left_height += 10;
			}
			if (mod_moreHealthEnhanced.renderCustomGUI) {
				if (!mod_moreHealthEnhanced.minimalisticGUI) {
					event.setCanceled(true);
					this.mc.mcProfiler.startSection("armor");

					ScaledResolution res = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
					int width = res.getScaledWidth();
					int height = res.getScaledHeight();

					int left = width / 2 - 91 + 100;
					int top = height - 49;

					int level = ForgeHooks.getTotalArmorValue(this.mc.thePlayer);
					for (int i = 1; (level > 0) && (i < 20); i += 2) {
						if (i < level) {
							drawTexturedModalRect(left, top, 34, 9, 9, 9);
						} else if (i == level) {
							drawTexturedModalRect(left, top, 25, 9, 9, 9);
						} else if (i > level) {
							drawTexturedModalRect(left, top, 16, 9, 9, 9);
						}
						left += 8;
					}
					this.mc.mcProfiler.endSection();
				}
			}
		}
	}

	@SubscribeEvent
	public void modifyHealthHUD(RenderGameOverlayEvent.Pre evt) {
		if (evt == null) {
			return;
		}
		if (evt.type == null) {
			return;
		}
		if (evt.type.equals(RenderGameOverlayEvent.ElementType.HEALTH)) {
			if (mod_moreHealthEnhanced.minimalisticGUI) {
				evt.setCanceled(true);

				this.mc.mcProfiler.startSection("health");

				boolean highlight = this.mc.thePlayer.hurtResistantTime / 3 % 2 == 1;
				if (this.mc.thePlayer.hurtResistantTime < 10) {
					highlight = false;
				}
				ScaledResolution res = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
				int width = res.getScaledWidth();
				int height = res.getScaledHeight();

				int health = MathHelper.ceiling_float_int(this.mc.thePlayer.getHealth());

				int healthLast = MathHelper.ceiling_float_int(this.mc.thePlayer.prevHealth);
				int left = width / 2 - 91;
				int top = height - 39;
				int colorX = left - 7;
				int colorY = top + 1;
				int regen = -1;
				if (this.mc.thePlayer.isPotionActive(Potion.regeneration)) {
					regen = this.mc.ingameGUI.getUpdateCounter() % 25;
				}
				int row = (health - 1) / 20;
				for (int i = row * 10; i < row * 10 + 10; i++) {
					if ((i + 1) * 2 <= this.mc.thePlayer.getMaxHealth()) {
						int idx = i * 2 + 1;
						int iconX = 16;
						if (this.mc.thePlayer.isPotionActive(Potion.poison)) {
							iconX += 36;
						} else if (this.mc.thePlayer.isPotionActive(Potion.wither)) {
							iconX += 72;
						}
						int x = left + i * 8 - 80 * row;
						int y = top;
						if (health <= 4) {
							y = top + this.rand.nextInt(2);
						}
						if (i == regen) {
							y -= 2;
						}
						byte iconY = 0;
						if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
							iconY = 5;
						}
						drawTexturedModalRect(x, y, 16 + (highlight ? 9 : 0), 9 * iconY, 9, 9);
						if (highlight) {
							if (idx < healthLast) {
								drawTexturedModalRect(x, y, iconX + 54, 9 * iconY, 9, 9);
							} else if (idx == healthLast) {
								drawTexturedModalRect(x, y, iconX + 63, 9 * iconY, 9, 9);
							}
						}
						if (idx < health) {
							drawTexturedModalRect(x, y, iconX + 36, 9 * iconY, 9, 9);
						} else if (idx == health) {
							drawTexturedModalRect(x, y, iconX + 45, 9 * iconY, 9, 9);
						}
						if (mod_moreHealthEnhanced.minimalisticGUI) {
							int displayedRow = row + 1;
							String text = "" + displayedRow;
							int adjustedColorX = colorX;
							if (displayedRow >= 10) {
								adjustedColorX -= 6;
							}
							if (displayedRow >= 100) {
								adjustedColorX -= 6;
							}
							if (displayedRow >= 1000) {
								adjustedColorX -= 6;
							}
							if (displayedRow >= 10000) {
								text = "9999+";
								adjustedColorX -= 6;
							}
							FontRenderer fontrenderer = this.mc.fontRenderer;
							fontrenderer.drawString(text, adjustedColorX + 1, colorY, 0);
							fontrenderer.drawString(text, adjustedColorX - 1, colorY, 0);
							fontrenderer.drawString(text, adjustedColorX, colorY + 1, 0);
							fontrenderer.drawString(text, adjustedColorX, colorY - 1, 0);
							fontrenderer.drawString(text, adjustedColorX, colorY, 15728640, false);

							GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
							bind(Gui.icons);
						}
					}
				}
				this.mc.mcProfiler.endSection();
			}
		}
	}
}
