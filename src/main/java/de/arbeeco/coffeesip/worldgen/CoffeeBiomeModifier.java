package de.arbeeco.coffeesip.worldgen;

import de.arbeeco.coffeesip.Coffee;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import org.quiltmc.qsl.worldgen.biome.api.BiomeModifications;
import org.quiltmc.qsl.worldgen.biome.api.BiomeSelectors;

public class CoffeeBiomeModifier {
	public static void setupFeatures() {
		BiomeModifications.addFeature(
				(context) -> context.isIn(TagKey.of(Registry.BIOME_KEY, new Identifier(Coffee.MOD_ID, "has_feature/patch_coffee_beans"))),
				GenerationStep.Feature.VEGETAL_DECORATION,
				RegistryKey.of(Registry.PLACED_FEATURE_KEY, new Identifier(Coffee.MOD_ID, "patch_coffee_beans"))
			);
	}
}
