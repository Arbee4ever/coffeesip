package de.arbeeco.coffeesip.blocks.entity;

import de.arbeeco.coffeesip.blocks.CoffeeBrewerBlock;
import de.arbeeco.coffeesip.client.gui.CoffeeBrewerScreenHandler;
import de.arbeeco.coffeesip.recipes.CoffeeBrewingRecipe;
import de.arbeeco.coffeesip.registries.CoffeeBlocks;
import de.arbeeco.coffeesip.registries.CoffeeRecipes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;

public class CoffeeBrewerBlockEntity extends LockableContainerBlockEntity implements SidedInventory {
	private DefaultedList<ItemStack> inventory;
	int brewTime = 0;
	private boolean[] slotsEmptyLastTick;
	int fuel;
	int reservedFuel = 0;
	int water;
	int reservedWater = 0;
	public final PropertyDelegate propertyDelegate;

	public ItemStack result = ItemStack.EMPTY;

	public CoffeeBrewerBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(CoffeeBlocks.COFFEE_BREWER_BLOCK_ENTITY, blockPos, blockState);
		this.inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
		this.propertyDelegate = new PropertyDelegate() {
			public int get(int index) {
				return switch (index) {
					case 0 -> brewTime;
					case 1 -> fuel;
					case 2 -> water;
					default -> 0;
				};
			}

			public void set(int index, int value) {
				switch (index) {
					case 0 -> brewTime = value;
					case 1 -> fuel = value;
					case 2 -> water = value;
				}
			}

			public int size() {
				return 3;
			}
		};
	}

	@Override
	protected Text getContainerName() {
		return Text.translatable("gui.coffeesip.coffee_brewer");
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new CoffeeBrewerScreenHandler(syncId, playerInventory, this, propertyDelegate);
	}

	@Override
	public int size() {
		return inventory.size();
	}

	@Override
	public boolean isEmpty() {
		Iterator var1 = inventory.iterator();

		ItemStack itemStack;
		do {
			if (!var1.hasNext()) {
				return true;
			}

			itemStack = (ItemStack) var1.next();
		} while (itemStack.isEmpty());

		return false;
	}

	public int getBrewTime() {
		return propertyDelegate.get(0);
	}

	public int getFuel() {
		return propertyDelegate.get(1);
	}

	public int getWater() {
		return propertyDelegate.get(2);
	}

	public static void tick(World world, BlockPos pos, BlockState state, CoffeeBrewerBlockEntity blockEntity) {
		ItemStack fuelStack = blockEntity.inventory.get(3);
		if (blockEntity.fuel <= 0 && fuelStack.isOf(Items.BLAZE_POWDER)) {
			blockEntity.fuel = 20;
			fuelStack.decrement(1);
			markDirty(world, pos, state);
		}
		ItemStack waterStack = blockEntity.inventory.get(4);
		if (blockEntity.water <= 0 && waterStack.isOf(Items.WATER_BUCKET)) {
			blockEntity.water = 1000;
			blockEntity.inventory.set(4, new ItemStack(Items.BUCKET));
			markDirty(world, pos, state);
		}

		if (blockEntity.brewTime > 0 && blockEntity.canCraft()) {
			--blockEntity.brewTime;
			if (blockEntity.brewTime == 0) {
				blockEntity.tryCraft();
			}
		} else if(blockEntity.canCraft()) {
			blockEntity.brewTime = 100;
		} else {
			blockEntity.brewTime = 0;
		}

		boolean[] bls = blockEntity.getSlotsEmpty();
		if (!Arrays.equals(bls, blockEntity.slotsEmptyLastTick)) {
			blockEntity.slotsEmptyLastTick = bls;
			BlockState blockState = state;
			if (!(state.getBlock() instanceof CoffeeBrewerBlock)) {
				return;
			}
			for (int i = 0; i < CoffeeBrewerBlock.CUP_PROPERTIES.length; ++i) {
				blockState = blockState.with(CoffeeBrewerBlock.CUP_PROPERTIES[i], bls[i]);
			}
			world.setBlockState(pos, blockState, 2);
		}
	}

	private boolean canCraft() {
		if (!PotionUtil.getPotion(getStack(0)).equals(Potions.EMPTY) || !PotionUtil.getPotion(getStack(1)).equals(Potions.EMPTY)) return false;
		var optional = world.getRecipeManager().getFirstMatch(CoffeeRecipes.COFFEE_BREWING, this, world);
		if (optional.isEmpty()) return false;
		reservedWater = optional.get().water();
		reservedFuel = optional.get().fuel();
		if (getFuel() - reservedFuel < 0 || getWater() - reservedWater < 0) return false;
		return optional.isPresent();
	}

	private void tryCraft() {
		var optional = world.getRecipeManager().getFirstMatch(CoffeeRecipes.COFFEE_BREWING, this, world);
		optional.ifPresent(this::startCrafting);
	}

	private void startCrafting(CoffeeBrewingRecipe coffeeBrewingRecipe) {
		boolean potion = false;
		result = coffeeBrewingRecipe.getOutput();
		water -= reservedWater;
		fuel -= reservedFuel;

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < coffeeBrewingRecipe.inputs().length - 1; j++) {
				if (coffeeBrewingRecipe.inputs()[j].test(getStack(i))) {
					ItemStack output = result.copy();
					if(getStack(2).isOf(Items.POTION)) {
						potion = true;
						PotionUtil.setPotion(output, PotionUtil.getPotion(getStack(2)));
					}
					setStack(i, output);
				}
			}
		}
		for (int i = 0; i < coffeeBrewingRecipe.inputs().length; i++) {
			if (coffeeBrewingRecipe.inputs()[i].test(new ItemStack(getStack(2).getItem()))) {
				getStack(2).decrement(coffeeBrewingRecipe.inputs()[i].getMatchingStacks()[0].getCount());
				if (potion) {
					setStack(2, new ItemStack(Items.GLASS_BOTTLE));
				}
			}
		}
		markDirty();
	}

	private boolean[] getSlotsEmpty() {
		boolean[] bls = new boolean[4];
		for (int i = 0; i < 3; ++i) {
			if (!(this.inventory.get(i)).isEmpty()) {
				bls[i] = true;
			}
		}
		return bls;
	}

	@Override
	public ItemStack getStack(int slot) {
		return slot >= 0 && slot < inventory.size() ? inventory.get(slot) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return Inventories.splitStack(inventory, slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		return Inventories.removeStack(inventory, slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		if (slot >= 0 && slot < inventory.size()) {
			inventory.set(slot, stack);
			markDirty();
		}
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		if (world.getBlockEntity(pos) != this) {
			return false;
		} else {
			return !(player.squaredDistanceTo((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5) > 64.0);
		}
	}

	@Override
	public void clear() {
		this.inventory.clear();
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		fuel = nbt.getByte("Fuel");
		water = nbt.getByte("Water");
		brewTime = nbt.getShort("BrewTime");
		Inventories.readNbt(nbt, inventory);
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putByte("Fuel", (byte) fuel);
		nbt.putByte("Water", (byte) water);
		nbt.putShort("BrewTime", (short) brewTime);
		Inventories.writeNbt(nbt, inventory);
	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.of(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return toNbt();
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return new int[0];
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		return false;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return false;
	}
}
