package de.arbeeco.coffeesip.recipes;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.recipe.api.serializer.QuiltRecipeSerializer;

public class CoffeeBrewingSerializer implements QuiltRecipeSerializer<CoffeeBrewingRecipe> {
	@Override
	public CoffeeBrewingRecipe read(Identifier id, JsonObject json) {
		var schema = gson.fromJson(json, JsonSchema.class);
		if (schema.fuel == null) {
			throw new JsonSyntaxException("Missing field 'fuel'");
		}
		if (schema.input == null) {
			throw new JsonSyntaxException("Missing field 'input'");
		}
		if (schema.result == null) {
			throw new JsonSyntaxException("Missing field 'result'");
		}
		var catalyst = Ingredient.fromJson(schema.fuel);
		var input = Ingredient.fromJson(schema.input);

		var result = ShapedRecipe.outputFromJson(schema.result);

		return new CoffeeBrewingRecipe(id, catalyst, input, result);
	}

	Gson gson = new Gson();

	@Override
	public JsonObject toJson(CoffeeBrewingRecipe recipe) {
		var obj = new JsonObject();
		obj.add("type", new JsonPrimitive(CoffeeBrewingRecipe.TYPE.id().toString()));
		obj.add("fuel", recipe.fuel().toJson());
		var input = new JsonArray();
		input.add(recipe.input().toJson());
		obj.add("input", input);
		ItemStack.CODEC.encode(recipe.result(), JsonOps.INSTANCE, JsonOps.INSTANCE.empty())
				.result()
				.ifPresent(result -> obj.add("result", result));
		return obj;
	}

	@Override
	public CoffeeBrewingRecipe read(Identifier id, PacketByteBuf buf) {
		var fuel = Ingredient.fromPacket(buf);
		var input = Ingredient.fromPacket(buf);
		var result = buf.readItemStack();
		return new CoffeeBrewingRecipe(id, fuel, input, result);
	}

	@Override
	public void write(PacketByteBuf buf, CoffeeBrewingRecipe recipe) {
		recipe.fuel().write(buf);
		recipe.input().write(buf);
		buf.writeItemStack(recipe.result());
	}

	public static class JsonSchema {
		public JsonObject fuel;
		public JsonObject input;
		public JsonObject result;
	}
}
