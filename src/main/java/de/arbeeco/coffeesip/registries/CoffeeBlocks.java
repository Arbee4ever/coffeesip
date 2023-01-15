package de.arbeeco.coffeesip.registries;

import de.arbeeco.coffeesip.Coffee;
import de.arbeeco.coffeesip.blocks.CoffeeBrewerBlock;
import de.arbeeco.coffeesip.blocks.CoffeeTreeBlock;
import de.arbeeco.coffeesip.blocks.CoffeeTreeUpperBlock;
import de.arbeeco.coffeesip.blocks.entity.CoffeeBrewerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

public class CoffeeBlocks {
	public static final CoffeeBrewerBlock COFFEE_BREWER_BLOCK = new CoffeeBrewerBlock(
			QuiltBlockSettings.of(Material.DECORATION)
					.sounds(BlockSoundGroup.METAL)
					.nonOpaque());
	public static final BlockEntityType<CoffeeBrewerBlockEntity> COFFEE_BREWER_BLOCK_ENTITY = Registry.register(
			Registry.BLOCK_ENTITY_TYPE, new Identifier(Coffee.MOD_ID, "coffee_brewer"),
			QuiltBlockEntityTypeBuilder.create(CoffeeBrewerBlockEntity::new, COFFEE_BREWER_BLOCK).build()
	);
	public static final CoffeeTreeBlock COFFEE_TREE = new CoffeeTreeBlock(
			QuiltBlockSettings.of(Material.PLANT)
					.noCollision()
					.ticksRandomly()
					.sounds(BlockSoundGroup.GRASS)
					.nonOpaque());
	public static final CoffeeTreeUpperBlock COFFEE_TREE_UPPER_BLOCK = new CoffeeTreeUpperBlock(
			QuiltBlockSettings.of(Material.PLANT)
					.noCollision()
					.ticksRandomly()
					.sounds(BlockSoundGroup.GRASS)
					.nonOpaque());

	public static void setupBlocks() {
		Registry.register(Registry.BLOCK, new Identifier(Coffee.MOD_ID, "coffee_brewer"), COFFEE_BREWER_BLOCK);
		Registry.register(Registry.BLOCK, new Identifier(Coffee.MOD_ID, "coffee_beans"), COFFEE_TREE);
		Registry.register(Registry.BLOCK, new Identifier(Coffee.MOD_ID, "coffee_beans_upper"), COFFEE_TREE_UPPER_BLOCK);
	}
}
