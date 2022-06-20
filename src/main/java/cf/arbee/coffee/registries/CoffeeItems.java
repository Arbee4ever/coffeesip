package cf.arbee.coffee.registries;

import cf.arbee.coffee.Coffee;
import cf.arbee.coffee.Items.CoffeeCup;
import cf.arbee.coffee.Items.Cup;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class CoffeeItems {
	public static final CoffeeCup COFFEE_CUP = new CoffeeCup(
			new QuiltItemSettings()
					.maxCount(1)
					.group(ItemGroup.FOOD)
					.food(
							new FoodComponent.Builder()
									.hunger(1)
									.saturationModifier(0)
									.alwaysEdible()
									.statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 600), 0.15f)
									.build()
					)
	);
	public static final Cup CUP = new Cup(
			new QuiltItemSettings()
					.maxCount(16)
					.group(ItemGroup.MISC)
	);
	public static final BlockItem COFFEE_BEANS = new BlockItem(
			CoffeeBlocks.COFFEE_TREE,
			new QuiltItemSettings()
					.group(ItemGroup.FOOD)
	);

	public static void setupItems() {
		Registry.register(Registry.ITEM, new Identifier(Coffee.MOD_ID, "coffee_cup"), COFFEE_CUP);
		Registry.register(Registry.ITEM, new Identifier(Coffee.MOD_ID, "cup"), CUP);
		Registry.register(Registry.ITEM, new Identifier(Coffee.MOD_ID, "coffee_beans"), COFFEE_BEANS);
	}
}
