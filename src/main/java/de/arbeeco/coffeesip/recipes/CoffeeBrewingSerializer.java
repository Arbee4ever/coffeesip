package de.arbeeco.coffeesip.recipes;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.recipe.api.serializer.QuiltRecipeSerializer;

import java.util.Arrays;

public class CoffeeBrewingSerializer implements QuiltRecipeSerializer<CoffeeBrewingRecipe> {
	Gson gson = new Gson();

	@Override
	public CoffeeBrewingRecipe read(Identifier id, JsonObject json) {
		var schema = gson.fromJson(json, JsonSchema.class);
		if (schema.input == null) {
			throw new JsonSyntaxException("Missing field 'input'");
		}
		if (schema.ingredient == null) {
			throw new JsonSyntaxException("Missing field 'ingredient'");
		}
		if (schema.result == null) {
			throw new JsonSyntaxException("Missing field 'result'");
		}
		var fuel = schema.fuel;
		var water = schema.water;
		var input = Ingredient.fromJson(schema.input);
		var ingredient = Ingredient.fromJson(schema.ingredient);

		var result = ShapedRecipe.outputFromJson(schema.result);

		return new CoffeeBrewingRecipe(id, fuel, water, input, ingredient, result);
	}

	@Override
	public CoffeeBrewingRecipe read(Identifier id, PacketByteBuf buf) {
		return null;
	}

	@Override
	public JsonObject toJson(CoffeeBrewingRecipe recipe) {
		var obj = new JsonObject();
		obj.add("type", new JsonPrimitive(CoffeeBrewingRecipe.TYPE.id().toString()));
		obj.add("fuel", new JsonPrimitive(recipe.fuel()));
		obj.add("water", new JsonPrimitive(recipe.water()));
		obj.add("input", recipe.input().toJson());
		obj.add("ingredient", recipe.ingredient().toJson());
		ItemStack.CODEC.encode(recipe.result(), JsonOps.INSTANCE, JsonOps.INSTANCE.empty())
				.result()
				.ifPresent(result -> obj.add("result", result));
		return obj;
	}

	@Override
	public void write(PacketByteBuf buf, CoffeeBrewingRecipe recipe) {}

	public static class JsonSchema {
		public int fuel;
		public int water;
		public JsonObject input;
		public JsonObject ingredient;
		public JsonObject result;
	}
}
