package de.arbeeco.coffee.world.worldgen;

import de.arbeeco.coffee.blocks.CoffeeTreeBlock;
import de.arbeeco.coffee.registries.CoffeeBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class CoffeeBeansFeature extends Feature<RandomPatchFeatureConfig> {
	BlockState coffee_beans = CoffeeBlocks.COFFEE_TREE.withAge(CoffeeTreeBlock.MAX_AGE);
	BlockState coffee_beans_upper = CoffeeBlocks.COFFEE_TREE_UPPER_BLOCK.withAge(CoffeeTreeBlock.MAX_AGE);
	public CoffeeBeansFeature(Codec<RandomPatchFeatureConfig> codec) {
		super(codec);
	}

	/*@Override
	public boolean place(FeatureContext<RandomPatchFeatureConfig> context) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for(int i = 0; i < context.getConfig().tries(); i++) {
			mutable.set(context.getOrigin()).move(
					context.getRandom().nextInt((context.getConfig().spreadXz() * 2) + 1) - context.getConfig().spreadXz(),
					context.getRandom().nextInt(3) - 1,
					context.getRandom().nextInt((context.getConfig().spreadXz() * 2) + 1) - context.getConfig().spreadXz()
			);

			if(context.getWorld().getBlockState(mutable).isAir()) {
				if((context.getRandom().nextFloat() < 0.45f || !context.getWorld().getBlockState(mutable.up()).isAir()) && coffee_beans.canPlaceAt(context.getWorld(), mutable)) {
					context.getWorld().setBlockState(mutable, coffee_beans, 3);
					context.getWorld().setBlockState(mutable.move(Direction.UP), coffee_beans_upper, 3);
				}
			}
		}

		return true;
	}*/

	@Override
	public boolean place(FeatureContext<RandomPatchFeatureConfig> context) {
		StructureWorldAccess level = context.getWorld();
		RandomPatchFeatureConfig config = context.getConfig();

		int i = 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int j = 0; j < config.tries(); ++j) {
			mutable.set(context.getOrigin()).move(
					context.getRandom().nextInt(config.spreadXz() + 1) - context.getRandom().nextInt(config.spreadXz() + 1),
					context.getRandom().nextInt(config.spreadY() + 1) - context.getRandom().nextInt(config.spreadY() + 1),
					context.getRandom().nextInt(config.spreadXz() + 1) - context.getRandom().nextInt(config.spreadXz() + 1)
			);

			if (level.getBlockState(mutable).getBlock() == Blocks.AIR && coffee_beans.canPlaceAt(level, mutable) && level.getBlockState(mutable.up()).getBlock() == Blocks.AIR && coffee_beans_upper.canPlaceAt(level, mutable)) {
				context.getWorld().setBlockState(mutable, coffee_beans, 3);
				context.getWorld().setBlockState(mutable.move(Direction.UP), coffee_beans_upper, 3);
				++i;
			}
		}

		return i > 0;
	}
}
