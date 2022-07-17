package de.arbeeco.coffeesip.client;

import de.arbeeco.coffeesip.registries.CoffeeBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;

@Environment(EnvType.CLIENT)
public class CoffeeClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		BlockRenderLayerMap.put(RenderLayer.getCutout(), CoffeeBlocks.COFFEE_TREE);
		BlockRenderLayerMap.put(RenderLayer.getCutout(), CoffeeBlocks.COFFEE_TREE_UPPER_BLOCK);
	}
}
