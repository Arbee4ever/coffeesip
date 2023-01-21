package de.arbeeco.coffeesip.recipes;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public record CoffeeBrewingRecipeType<T extends Recipe<?>>(Identifier id) implements RecipeType<T> {
	@Override
	public String toString() {
		return id.toString();
	}
}
