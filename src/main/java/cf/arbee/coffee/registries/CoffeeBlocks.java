package cf.arbee.coffee.registries;

import cf.arbee.coffee.Coffee;
import cf.arbee.coffee.blocks.CoffeeTreeBlock;
import cf.arbee.coffee.blocks.CoffeeTreeUpperBlock;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

public class CoffeeBlocks {
	public static final CoffeeTreeBlock COFFEE_TREE = new CoffeeTreeBlock(
			QuiltBlockSettings.of(Material.PLANT)
					.noCollision()
					.sounds(BlockSoundGroup.GRASS)
					.nonOpaque());
	public static final CoffeeTreeUpperBlock COFFEE_TREE_UPPER_BLOCK = new CoffeeTreeUpperBlock(
			QuiltBlockSettings.of(Material.PLANT)
					.noCollision()
					.sounds(BlockSoundGroup.GRASS)
					.nonOpaque());

	public static void setupBlocks() {
		Registry.register(Registry.BLOCK, new Identifier(Coffee.MOD_ID, "coffee_beans"), COFFEE_TREE);
		Registry.register(Registry.BLOCK, new Identifier(Coffee.MOD_ID, "coffee_beans_upper"), COFFEE_TREE_UPPER_BLOCK);
	}
}
