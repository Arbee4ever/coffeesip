package de.arbeeco.coffeesip.registries;

import de.arbeeco.coffeesip.Coffee;
import de.arbeeco.coffeesip.items.CoffeeCup;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class CoffeeItems {
	public static final BlockItem COFFEE_BREWER_ITEM = new BlockItem(
			CoffeeBlocks.COFFEE_BREWER_BLOCK,
			new QuiltItemSettings()
					.group(ItemGroup.BREWING)
	);
	public static final CoffeeCup COFFEE_CUP = new CoffeeCup(
			new QuiltItemSettings()
					.maxCount(1)
					.group(ItemGroup.BREWING)
					.food(
							new FoodComponent.Builder()
									.saturationModifier(0)
									.alwaysEdible()
									.build()
					)
	);
	public static final Item CUP = new Item(
			new QuiltItemSettings()
					.maxCount(16)
					.group(ItemGroup.MISC)
	);
	public static final BlockItem COFFEE_BEANS = new BlockItem(
			CoffeeBlocks.COFFEE_TREE,
			new QuiltItemSettings()
					.group(ItemGroup.FOOD)
	);
	public static final Item ROASTED_COFFEE_BEANS = new Item(
			new QuiltItemSettings()
					.group(ItemGroup.FOOD)
	);
	public static final Item COFFEE_POWDER = new Item(
			new QuiltItemSettings()
					.group(ItemGroup.BREWING)
	);

	public static void setupItems() {
		Registry.register(Registry.ITEM, new Identifier(Coffee.MOD_ID, "coffee_brewer"), COFFEE_BREWER_ITEM);
		Registry.register(Registry.ITEM, new Identifier(Coffee.MOD_ID, "coffee_powder"), COFFEE_POWDER);
		Registry.register(Registry.ITEM, new Identifier(Coffee.MOD_ID, "roasted_coffee_beans"), ROASTED_COFFEE_BEANS);
		Registry.register(Registry.ITEM, new Identifier(Coffee.MOD_ID, "coffee_beans"), COFFEE_BEANS);
		Registry.register(Registry.ITEM, new Identifier(Coffee.MOD_ID, "coffee_cup"), COFFEE_CUP);
		Registry.register(Registry.ITEM, new Identifier(Coffee.MOD_ID, "cup"), CUP);
	}
}
