package de.arbeeco.coffeesip.blocks.entity;

import de.arbeeco.coffeesip.client.gui.CoffeeBrewerScreenHandler;
import de.arbeeco.coffeesip.registries.CoffeeBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;

public class CoffeeBrewerBlockEntity extends LockableContainerBlockEntity implements SidedInventory {
	private static final int INGREDIENT_SLOT = 3;
	private static final int FUEL_SLOT = 4;
	private static final int[] TOP_SLOTS = new int[]{3};
	private static final int[] BOTTOM_SLOTS = new int[]{0, 1, 2, 3};
	private static final int[] SIDE_SLOTS = new int[]{0, 1, 2, 4};
	public static final int FUEL_USES = 20;
	public static final int BREW_TIME_DATA = 0;
	public static final int FUEL_DATA = 1;
	public static final int DATA_VALUES = 2;
	private DefaultedList<ItemStack> inventory;
	int brewTime;
	private boolean[] slotsEmptyLastTick;
	private Item itemBrewing;
	int fuel;
	protected final PropertyDelegate propertyDelegate;

	public CoffeeBrewerBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(CoffeeBlocks.COFFEE_BREWER_BLOCK_ENTITY, blockPos, blockState);
		this.inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
		this.propertyDelegate = new PropertyDelegate() {
			public int get(int index) {
				switch (index) {
					case 0:
						return brewTime;
					case 1:
						return fuel;
					default:
						return 0;
				}
			}

			public void set(int index, int value) {
				switch (index) {
					case 0:
						brewTime = value;
						break;
					case 1:
						fuel = value;
				}
			}

			public int size() {
				return 2;
			}
		};
	}

	protected Text getContainerName() {
		return Text.translatable("gui.coffeesip.coffee_brewer");
	}

	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new CoffeeBrewerScreenHandler(syncId, playerInventory);
	}

	public int size() {
		return this.inventory.size();
	}

	public boolean isEmpty() {
		Iterator var1 = this.inventory.iterator();

		ItemStack itemStack;
		do {
			if (!var1.hasNext()) {
				return true;
			}

			itemStack = (ItemStack) var1.next();
		} while (itemStack.isEmpty());

		return false;
	}

	public static void tick(World world, BlockPos pos, BlockState state, CoffeeBrewerBlockEntity blockEntity) {
		ItemStack itemStack = (ItemStack)blockEntity.inventory.get(4);
		if (blockEntity.fuel <= 0 && itemStack.isOf(Items.BLAZE_POWDER)) {
			blockEntity.fuel = 20;
			itemStack.decrement(1);
			markDirty(world, pos, state);
		}

		boolean bl = canCraft(blockEntity.inventory);
		boolean bl2 = blockEntity.brewTime > 0;
		ItemStack itemStack2 = (ItemStack)blockEntity.inventory.get(3);
		if (bl2) {
			--blockEntity.brewTime;
			boolean bl3 = blockEntity.brewTime == 0;
			if (bl3 && bl) {
				craft(world, pos, blockEntity.inventory);
				markDirty(world, pos, state);
			} else if (!bl || !itemStack2.isOf(blockEntity.itemBrewing)) {
				blockEntity.brewTime = 0;
				markDirty(world, pos, state);
			}
		} else if (bl && blockEntity.fuel > 0) {
			--blockEntity.fuel;
			blockEntity.brewTime = 400;
			blockEntity.itemBrewing = itemStack2.getItem();
			markDirty(world, pos, state);
		}

		boolean[] bls = blockEntity.getSlotsEmpty();
		if (!Arrays.equals(bls, blockEntity.slotsEmptyLastTick)) {
			blockEntity.slotsEmptyLastTick = bls;
			BlockState blockState = state;
			if (!(state.getBlock() instanceof BrewingStandBlock)) {
				return;
			}

			for(int i = 0; i < BrewingStandBlock.BOTTLE_PROPERTIES.length; ++i) {
				blockState = blockState.with(BrewingStandBlock.BOTTLE_PROPERTIES[i], bls[i]);
			}

			world.setBlockState(pos, blockState, 2);
		}
	}

	private boolean[] getSlotsEmpty() {
		boolean[] bls = new boolean[3];
		for(int i = 0; i < 3; ++i) {
			if (!(this.inventory.get(i)).isEmpty()) {
				bls[i] = true;
			}
		}
		return bls;
	}

	private static boolean canCraft(DefaultedList<ItemStack> slots) {
		ItemStack itemStack = slots.get(3);
		if (itemStack.isEmpty()) {
			return false;
		} else {
			for(int i = 0; i < 3; ++i) {
				ItemStack itemStack2 = slots.get(i);
				if (!itemStack2.isEmpty() && BrewingRecipeRegistry.hasRecipe(itemStack2, itemStack)) {
					return true;
				}
			}

			return false;
		}
	}

	private static void craft(World world, BlockPos pos, DefaultedList<ItemStack> slots) {
		ItemStack itemStack = slots.get(3);

		for(int i = 0; i < 3; ++i) {
			slots.set(i, BrewingRecipeRegistry.craft(itemStack, slots.get(i)));
		}

		itemStack.decrement(1);
		if (itemStack.getItem().hasRecipeRemainder()) {
			ItemStack itemStack2 = new ItemStack(itemStack.getItem().getRecipeRemainder());
			if (itemStack.isEmpty()) {
				itemStack = itemStack2;
			} else {
				ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), itemStack2);
			}
		}

		slots.set(3, itemStack);
		world.syncWorldEvent(1035, pos, 0);
	}

	public ItemStack getStack(int slot) {
		return slot >= 0 && slot < inventory.size() ? inventory.get(slot) : ItemStack.EMPTY;
	}

	public ItemStack removeStack(int slot, int amount) {
		return Inventories.splitStack(inventory, slot, amount);
	}

	public ItemStack removeStack(int slot) {
		return Inventories.removeStack(inventory, slot);
	}

	public void setStack(int slot, ItemStack stack) {
		if (slot >= 0 && slot < inventory.size()) {
			inventory.set(slot, stack);
		}
	}

	public boolean canPlayerUse(PlayerEntity player) {
		if (world.getBlockEntity(pos) != this) {
			return false;
		} else {
			return !(player.squaredDistanceTo((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5) > 64.0);
		}
	}

	public void clear() {
		this.inventory.clear();
	}

	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		Inventories.readNbt(nbt, inventory);
	}

	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		Inventories.writeNbt(nbt, inventory);
	}

	public int[] getAvailableSlots(Direction side) {
		return new int[0];
	}

	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		return false;
	}

	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return false;
	}
}
