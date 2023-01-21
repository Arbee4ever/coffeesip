package de.arbeeco.coffeesip;

import de.arbeeco.coffeesip.registries.CoffeeBlocks;
import de.arbeeco.coffeesip.registries.CoffeeItems;
import de.arbeeco.coffeesip.registries.CoffeeRecipes;
import de.arbeeco.coffeesip.worldgen.CoffeeBiomeModifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Coffee implements ModInitializer {
	public static final String MOD_ID = "coffeesip";
	public static final Logger LOGGER = LoggerFactory.getLogger("Coffee");

	@Override
	public void onInitialize(ModContainer mod) {
		CoffeeItems.setupItems();
		CoffeeBlocks.setupBlocks();
		CoffeeRecipes.setupRecipes();
		CoffeeBiomeModifier.setupFeatures();
	}
}
