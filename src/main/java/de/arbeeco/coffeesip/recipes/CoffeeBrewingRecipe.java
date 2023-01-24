package de.arbeeco.coffeesip.recipes;

import de.arbeeco.coffeesip.Coffee;
import de.arbeeco.coffeesip.blocks.entity.CoffeeBrewerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Arrays;

public record CoffeeBrewingRecipe(Identifier id, int fuel, int water, Ingredient input, Ingredient ingredient,
								  ItemStack result) implements Recipe<Inventory> {
	public static final CoffeeBrewingRecipeType<CoffeeBrewingRecipe> TYPE = new CoffeeBrewingRecipeType<>(new Identifier(Coffee.MOD_ID, "coffee_brewing"));

	@Override
	public boolean matches(Inventory inventory, World world) {
		if (inventory.getStack(1).isOf(Items.AIR) && inventory.getStack(0).isOf(Items.AIR)) {
			return false;
		}
		if (!(input().test(inventory.getStack(0)) || inventory.getStack(0).isOf(Items.AIR))) {
			return false;
		}
		if (!(input().test(inventory.getStack(1)) || inventory.getStack(1).isOf(Items.AIR))) {
			return false;
		}
		return ingredient.test(inventory.getStack(2));
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
