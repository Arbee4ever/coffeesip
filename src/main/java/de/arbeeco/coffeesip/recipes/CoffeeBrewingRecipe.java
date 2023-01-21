package de.arbeeco.coffeesip.recipes;

import de.arbeeco.coffeesip.Coffee;
import de.arbeeco.coffeesip.blocks.entity.CoffeeBrewerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Arrays;

public record CoffeeBrewingRecipe(Identifier id, int fuel, int water, Ingredient[] inputs,
								  ItemStack result) implements Recipe<Inventory> {
	public static final CoffeeBrewingRecipeType<CoffeeBrewingRecipe> TYPE = new CoffeeBrewingRecipeType<>(new Identifier(Coffee.MOD_ID, "coffee_brewing"));

	@Override
	public boolean matches(Inventory inventory, World world) {
		var met = new boolean[]{false, false, false};
		for (var i = 0; i < inventory.size(); i++) {
			for (var j = 0; j < inputs().length; j++) {
				if (!met[j]) {
					met[j] = inputs()[j].test(inventory.getStack(i));
					if (met[j]) {
						break;
					} else if (j >= inputs().length - 1) {
						return false;
					}
				}
			}
		}
		return Arrays.equals(met, new boolean[]{true, true, true});
	}

	@Override
	public ItemStack craft(Inventory inventory) {
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
