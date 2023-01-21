package de.arbeeco.coffeesip.blocks;

import de.arbeeco.coffeesip.blocks.entity.CoffeeBrewerBlockEntity;
import de.arbeeco.coffeesip.registries.CoffeeBlocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CoffeeBrewerBlock extends BlockWithEntity {
	public static final BooleanProperty HAS_CUP_0 = BooleanProperty.of("has_cup_0");
	public static final BooleanProperty HAS_CUP_1 = BooleanProperty.of("has_cup_1");
	public static final BooleanProperty[] CUP_PROPERTIES = new BooleanProperty[]{HAS_CUP_0, HAS_CUP_1};
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

	public CoffeeBrewerBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState()
				.with(FACING, Direction.NORTH)
				.with(CUP_PROPERTIES[0], false)
				.with(CUP_PROPERTIES[1], false)
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Direction dir = state.get(FACING);
		switch (dir) {
			case NORTH:
				return VoxelShapes.union(
						Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
						Block.createCuboidShape(2.0, 0.0, 8.0, 14.0, 13.0, 14.0),
						Block.createCuboidShape(2.0, 9.0, 3.0, 14.0, 13.0, 14.0));
			case SOUTH:
				return VoxelShapes.union(
						Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
						Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 13.0, 8.0),
						Block.createCuboidShape(2.0, 9.0, 2.0, 14.0, 13.0, 13.0));
			case EAST:
				return VoxelShapes.union(
						Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
						Block.createCuboidShape(2.0, 0.0, 2.0, 8.0, 13.0, 14.0),
						Block.createCuboidShape(2.0, 9.0, 2.0, 13.0, 13.0, 14.0));
			case WEST:
				return VoxelShapes.union(
						Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
						Block.createCuboidShape(8.0, 0.0, 2.0, 14.0, 13.0, 14.0),
						Block.createCuboidShape(3.0, 9.0, 2.0, 14.0, 13.0, 14.0));
			default:
				return VoxelShapes.fullCube();
		}
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? null : checkType(type, CoffeeBlocks.COFFEE_BREWER_BLOCK_ENTITY, CoffeeBrewerBlockEntity::tick);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, CUP_PROPERTIES[0], CUP_PROPERTIES[1]);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof CoffeeBrewerBlockEntity) {
				player.openHandledScreen((CoffeeBrewerBlockEntity) blockEntity);
			}
			return ActionResult.CONSUME;
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof CoffeeBrewerBlockEntity) {
				ItemScatterer.spawn(world, pos, (CoffeeBrewerBlockEntity)blockEntity);
			}
			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new CoffeeBrewerBlockEntity(pos, state);
	}
}
