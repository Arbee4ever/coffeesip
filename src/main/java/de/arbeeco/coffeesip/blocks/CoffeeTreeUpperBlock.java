package de.arbeeco.coffeesip.blocks;

import de.arbeeco.coffeesip.registries.CoffeeBlocks;
import de.arbeeco.coffeesip.registries.CoffeeItems;
import net.minecraft.block.*;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class CoffeeTreeUpperBlock extends CropBlock implements Fertilizable {
	public static final IntProperty COFFEE_AGE = Properties.AGE_3;
	private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
			Block.createCuboidShape(3.d, .0d, 3.d, 13.d, 8.d, 13.d),
			Block.createCuboidShape(3.d, .0d, 3.d, 13.d, 10.d, 13.d),
			Block.createCuboidShape(2.d, .0d, 2.d, 14.d, 12.d, 14.d),
			Block.createCuboidShape(1.d, .0d, 1.d, 15.d, 16.d, 15.d)
	};

	public CoffeeTreeUpperBlock(Settings settings) {
		super(settings);
	}

	@Override
	public IntProperty getAgeProperty() {
		return COFFEE_AGE;
	}

	@Override
	public int getMaxAge() {
		return 3;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(COFFEE_AGE);
	}

	@Override
	protected ItemConvertible getSeedsItem() {
		return CoffeeItems.COFFEE_BEANS;
	}

	@Override
	protected int getGrowthAmount(World world) {
		return MathHelper.nextInt(world.getRandom(), 1, 4);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return (world.getLightLevel(pos, 0) >= 8 || world.isSkyVisible(pos)) && world.getBlockState(pos.down()).isOf(CoffeeBlocks.COFFEE_TREE);
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return floor.isOf(CoffeeBlocks.COFFEE_TREE);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE_BY_AGE[state.get(getAgeProperty())];
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (getAge(state) == getMaxAge()) {
			ItemStack itemStack = new ItemStack(CoffeeItems.COFFEE_BEANS);
			world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack));
		}
		super.onBreak(world, pos, state, player);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (getAge(state) == getMaxAge()) {
			ItemStack itemStack = new ItemStack(CoffeeItems.COFFEE_BEANS);
			world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack));
			world.setBlockState(pos, withAge(getMaxAge() - 1));
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}
}
