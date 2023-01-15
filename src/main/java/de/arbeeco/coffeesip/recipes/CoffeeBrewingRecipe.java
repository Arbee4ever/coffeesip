package de.arbeeco.coffeesip.recipes;

import de.arbeeco.coffeesip.Coffee;
import de.arbeeco.coffeesip.blocks.entity.CoffeeBrewerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public record CoffeeBrewingRecipe(Identifier id, Ingredient fuel, Ingredient input, ItemStack result) implements Recipe<CoffeeBrewerBlockEntity> {
	public static final CoffeeBrewingRecipeType<CoffeeBrewingRecipe> TYPE = new CoffeeBrewingRecipeType<>(new Identifier(Coffee.MOD_ID, "coffee_brewing"));
	@Override
	public boolean matches(CoffeeBrewerBlockEntity inventory, World world) {
		if(!fuel.test(inventory.getStack(4))) return false;
		return input.test(inventory.getStack(3));
	}

	@Override
	public ItemStack craft(CoffeeBrewerBlockEntity inventory) {
		return result.copy();
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getOutput() {
		return result;
	}

	@Override
	public Identifier getId() {
		return new Identifier(Coffee.MOD_ID, "coffee_brewing");
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return new CoffeeBrewingSerializer();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE;
	}
}
