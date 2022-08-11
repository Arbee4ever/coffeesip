package de.arbeeco.coffeesip.blocks;

import de.arbeeco.coffeesip.registries.CoffeeBlocks;
import de.arbeeco.coffeesip.registries.CoffeeItems;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.OrderedTick;

public class CoffeeTreeBlock extends PlantBlock implements Fertilizable {
	public static final IntProperty AGE = Properties.AGE_3;
	public static final BooleanProperty SUPPORTING = BooleanProperty.of("supporting");
	public static final int MAX_AGE = 3;

	private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[] {
			Block.createCuboidShape(3.d, .0d, 3.d, 13.d, 8.d, 13.d),
			Block.createCuboidShape(3.d, .0d, 3.d, 13.d, 10.d, 13.d),
			Block.createCuboidShape(2.d, .0d, 2.d, 14.d, 12.d, 14.d),
			Block.createCuboidShape(1.d, .0d, 1.d, 15.d, 16.d, 15.d)
	};

	public CoffeeTreeBlock(Settings settings) {
		super(settings);
		setDefaultState(getStateManager().getDefaultState().with(AGE, 0).with(SUPPORTING, false));
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		BlockState upperState = world.getBlockState(pos.up());
		if (upperState.getBlock() instanceof CoffeeTreeUpperBlock coffeeTreeUpperBlock) {
			return !coffeeTreeUpperBlock.isMature(upperState);
		}

		return true;
	}

	@Override
	public boolean canGrow(World world, RandomGenerator random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, RandomGenerator random, BlockPos pos, BlockState state) {
		int ageGrowth = Math.min(getAge(state) + getBonemealAgeIncrease(world), 7);
		if (ageGrowth <= MAX_AGE) {
			world.setBlockState(pos, state.with(AGE, ageGrowth));
		} else {
			BlockState top = world.getBlockState(pos.up());
			if (top.getBlock() == CoffeeBlocks.COFFEE_TREE_UPPER_BLOCK) {
				Fertilizable growable = (Fertilizable) world.getBlockState(pos.up()).getBlock();
				if (growable.isFertilizable(world, pos.up(), top, false)) {
					growable.grow(world, world.getRandom(), pos.up(), top);
				}
			} else {
				CoffeeTreeUpperBlock coffeeTreeUpperBlock = CoffeeBlocks.COFFEE_TREE_UPPER_BLOCK;
				int remainingGrowth = ageGrowth - MAX_AGE - 1;
				if (coffeeTreeUpperBlock.getDefaultState().canPlaceAt(world, pos.up()) && world.isAir(pos.up())) {
					world.setBlockState(pos, state.with(AGE, MAX_AGE));
					world.setBlockState(pos.up(), coffeeTreeUpperBlock.getDefaultState().with(CoffeeTreeUpperBlock.COFFEE_AGE, remainingGrowth), 2);
				}
			}
		}
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return super.canPlaceAt(state, world, pos);
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return super.canPlantOnTop(floor, world, pos);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(AGE, SUPPORTING);
	}

	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(CoffeeItems.COFFEE_BEANS);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		BlockState superState = super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
		if (!superState.isAir()) {
			world.getFluidTickScheduler().scheduleTick(OrderedTick.create(Fluids.WATER, pos));
			if (direction == Direction.UP) {
				return superState.with(SUPPORTING, isSupportingCoffeeUpper(newState));
			}
		}

		return superState;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
		super.randomTick(state, world, pos, random);

		if (!world.isRegionLoaded(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
			return;
		}

		if (world.getLightLevel(pos.up(), 0) >= 6 && getAge(state) <= MAX_AGE && random.nextInt(3) == 0) {
			randomGrowTick(state, world, pos);
		}
	}

	private void randomGrowTick(BlockState state, ServerWorld world, BlockPos pos) {
		int currentAge = getAge(state);
		if (currentAge == MAX_AGE) {
			CoffeeTreeUpperBlock coffeeTreeUpperBlock = CoffeeBlocks.COFFEE_TREE_UPPER_BLOCK;
			if (coffeeTreeUpperBlock.getDefaultState().canPlaceAt(world, pos.up()) && world.isAir(pos.up())) {
				world.setBlockState(pos.up(), coffeeTreeUpperBlock.getDefaultState());
			}
		} else {
			world.setBlockState(pos, withAge(currentAge + 1), 2);
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE_BY_AGE[getAge(state)];
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if(!player.isCreative()) {
			ItemStack itemStack = new ItemStack(CoffeeItems.COFFEE_BEANS);
			if (world.getBlockState(pos.up()).isOf(CoffeeBlocks.COFFEE_TREE_UPPER_BLOCK.withAge(3).getBlock())) {
				world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), itemStack));
			}
			world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack));
		}
		super.onBreak(world, pos, state, player);
	}

	public boolean isSupportingCoffeeUpper(BlockState topState) {
		return topState.isOf(CoffeeBlocks.COFFEE_TREE_UPPER_BLOCK);
	}

	public BlockState withAge(int age) {
		return getDefaultState().with(AGE, age);
	}

	protected int getAge(BlockState state) {
		return state.get(AGE);
	}

	protected int getBonemealAgeIncrease(World world) {
		return MathHelper.nextInt(world.getRandom(), 1, 4);
	}

}
