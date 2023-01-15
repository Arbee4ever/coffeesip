package de.arbeeco.coffeesip.registries;

import de.arbeeco.coffeesip.Coffee;
import de.arbeeco.coffeesip.recipes.CoffeeBrewingRecipe;
import de.arbeeco.coffeesip.recipes.CoffeeBrewingSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CoffeeRecipes {
	public static void setupRecipes() {
		Registry.register(Registry.RECIPE_TYPE, CoffeeBrewingRecipe.TYPE.id(), CoffeeBrewingRecipe.TYPE);
		Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(Coffee.MOD_ID, "coffee_brewing"), new CoffeeBrewingSerializer());
	}
}
