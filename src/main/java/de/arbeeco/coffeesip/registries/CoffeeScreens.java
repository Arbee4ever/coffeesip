package de.arbeeco.coffeesip.registries;

import de.arbeeco.coffeesip.Coffee;
import de.arbeeco.coffeesip.client.gui.CoffeeBrewerScreen;
import de.arbeeco.coffeesip.client.gui.CoffeeBrewerScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class CoffeeScreens {
	public static final ScreenHandlerType<CoffeeBrewerScreenHandler> COFFEE_BREWER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(
			new Identifier(Coffee.MOD_ID, "coffee_brewer"),
			CoffeeBrewerScreenHandler::new
	);

	public static void setupScreens() {
		HandledScreens.register(COFFEE_BREWER_SCREEN_HANDLER, CoffeeBrewerScreen::new);
	}
}
