package de.arbeeco.coffeesip.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import de.arbeeco.coffeesip.Coffee;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CoffeeBrewerScreen extends HandledScreen<CoffeeBrewerScreenHandler> {
	private static final Identifier TEXTURE = new Identifier(Coffee.MOD_ID, "textures/gui/container/coffee_brewer.png");
	private static final int[] BUBBLE_PROGRESS = new int[]{29, 24, 20, 16, 11, 6, 0};

	public CoffeeBrewerScreen(CoffeeBrewerScreenHandler coffeeBrewerScreenHandler, PlayerInventory playerInventory, Text text) {
		super(coffeeBrewerScreenHandler, playerInventory, text);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int i = (width - backgroundWidth) / 2;
		int j = (height - backgroundHeight) / 2;
		this.drawTexture(matrices, i, j, 0, 0, backgroundWidth, backgroundHeight);
		double fuel = handler.getFuel();
		double fuelMax = 20;
		double water = handler.getWater();
		double waterMax = 1000;
		double fuelWidth = 18 * (fuel / fuelMax);
		double waterWidth = 18 * (water / waterMax);
		if (fuelWidth > 0) {
			this.drawTexture(matrices, i + 60, j + 44, 176, 29, (int) fuelWidth, 4);
		}
		if (waterWidth > 0) {
			this.drawTexture(matrices, i + 96, j + 44, 194, 29, (int) waterWidth, 4);
		}

		int m = handler.getBrewTime();
		if (m > 0) {
			int n = (int) (28.0F * (1.0F - (float) m / 400.0F));
			if (n > 0) {
				drawTexture(matrices, i + 97, j + 15, 176, 0, 9, n);
			}

			n = BUBBLE_PROGRESS[m / 2 % 7];
			if (n > 0) {
				drawTexture(matrices, i + 63, j + 14 + 29 - n, 185, 29 - n, 12, n);
			}
		}

	}
}
