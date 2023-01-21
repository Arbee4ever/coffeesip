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
		if (schema.inputs == null) {
			throw new JsonSyntaxException("Missing field 'input'");
		}
		if (schema.result == null) {
			throw new JsonSyntaxException("Missing field 'result'");
		}
		var fuel = schema.fuel;
		var water = schema.water;
		var inputs = new Ingredient[schema.inputs.length];
		for (int i = 0; i < schema.inputs.length; i++) {
			inputs[i] = Ingredient.fromJson(schema.inputs[i]);
		}

		var result = ShapedRecipe.outputFromJson(schema.result);

		return new CoffeeBrewingRecipe(id, fuel, water, inputs, result);
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
		var inputs = new JsonArray();
		for (var input : recipe.inputs()) {
			inputs.add(input.toJson());
		}
		obj.add("inputs", inputs);
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
		public JsonObject[] inputs;
		public JsonObject result;
	}
}
