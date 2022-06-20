package cf.arbee.coffee;

import cf.arbee.coffee.registries.CoffeeBlocks;
import cf.arbee.coffee.registries.CoffeeItems;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Coffee implements ModInitializer {
	public static final String MOD_ID = "coffee";
	public static final Logger LOGGER = LoggerFactory.getLogger("Coffee");

	@Override
	public void onInitialize(ModContainer mod) {
		CoffeeItems.setupItems();
		CoffeeBlocks.setupBlocks();
	}
}
