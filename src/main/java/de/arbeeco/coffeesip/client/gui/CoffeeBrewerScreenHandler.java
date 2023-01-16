package de.arbeeco.coffeesip.client.gui;

import de.arbeeco.coffeesip.registries.CoffeeItems;
import de.arbeeco.coffeesip.registries.CoffeeScreens;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public class CoffeeBrewerScreenHandler extends ScreenHandler {
	private final Inventory inventory;
	private final PropertyDelegate propertyDelegate;
	private final Slot ingredientSlot;
	public CoffeeBrewerScreenHandler(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, new SimpleInventory(5), new ArrayPropertyDelegate(3));
	}

	public CoffeeBrewerScreenHandler(int i, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(CoffeeScreens.COFFEE_BREWER_SCREEN_HANDLER, i);
		checkSize(inventory, 5);
		checkDataCount(propertyDelegate, 3);
		this.inventory = inventory;
		this.propertyDelegate = propertyDelegate;
		addSlot(new CupSlot(inventory, 0, 67, 51));
		addSlot(new CupSlot(inventory, 1, 91, 51));
		ingredientSlot = addSlot(new IngredientSlot(inventory, 2, 79, 17));
		addSlot(new FuelSlot(inventory, 3, 17, 17));
		addSlot(new WaterSlot(inventory, 4, 141, 17));
		addProperties(propertyDelegate);

		int j;
		for(j = 0; j < 3; ++j) {
			for(int k = 0; k < 9; ++k) {
				addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for(j = 0; j < 9; ++j) {
			addSlot(new Slot(playerInventory, j, 8 + j * 18, 142));
		}

	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if ((index < 0 || index > 2) && index != 3 && index != 4) {
				if (FuelSlot.matches(itemStack)) {
					if (insertItem(itemStack2, 3, 4, false) || ingredientSlot.canInsert(itemStack2) && !insertItem(itemStack2, 3, 4, false)) {
						return ItemStack.EMPTY;
					}
				} else if (WaterSlot.matches(itemStack)) {
					if (insertItem(itemStack2, 4, 5, false) || ingredientSlot.canInsert(itemStack2) && !insertItem(itemStack2, 4, 5, false)) {
						return ItemStack.EMPTY;
					}
				} else if (ingredientSlot.canInsert(itemStack2)) {
					if (!insertItem(itemStack2, 2, 3, false)) {
						return ItemStack.EMPTY;
					}
				} else if (CupSlot.matches(itemStack) && itemStack.getCount() == 1) {
					if (!insertItem(itemStack2, 0, 2, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 5 && index < 32) {
					if (!insertItem(itemStack2, 32, 40, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 32 && index < 40) {
					if (!insertItem(itemStack2, 5, 32, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!insertItem(itemStack2, 5, 40, false)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (!insertItem(itemStack2, 5, 41, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickTransfer(itemStack2, itemStack);
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(player, itemStack2);
		}

		return itemStack;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return inventory.canPlayerUse(player);
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

	@Override
	protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
		if (startIndex == 0 && slots.get(startIndex).hasStack()) {
			startIndex = 1;
		}
		if (startIndex == 1 && slots.get(startIndex).hasStack()) {
			return false;
		}
		return super.insertItem(stack, startIndex, endIndex, fromLast);
	}

	static class CupSlot extends Slot {
		public CupSlot(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public int getMaxItemCount() {
			return 1;
		}

		@Override
		public void onTakeItem(PlayerEntity player, ItemStack stack) {
			Potion potion = PotionUtil.getPotion(stack);
			if (player instanceof ServerPlayerEntity) {
				Criteria.BREWED_POTION.trigger((ServerPlayerEntity)player, potion);
			}

			super.onTakeItem(player, stack);
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			if (this.getStack().isOf(Items.AIR)) {
				return matches(stack);
			}
			return false;
		}

		public static boolean matches(ItemStack stack) {
			return stack.isOf(CoffeeItems.COFFEE_CUP) || stack.isOf(CoffeeItems.CUP);
		}
	}

	private static class IngredientSlot extends Slot {
		public IngredientSlot(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return stack.isOf(CoffeeItems.COFFEE_BEANS);
		}

		@Override
		public int getMaxItemCount() {
			return 64;
		}
	}

	private static class FuelSlot extends Slot {
		public FuelSlot(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return matches(stack);
		}

		public static boolean matches(ItemStack stack) {
			return stack.isOf(Items.BLAZE_POWDER);
		}

		@Override
		public int getMaxItemCount() {
			return 64;
		}
	}

	private static class WaterSlot extends Slot {
		public WaterSlot(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return matches(stack);
		}

		public static boolean matches(ItemStack stack) {
			return stack.isOf(Items.WATER_BUCKET);
		}

		@Override
		public int getMaxItemCount() {
			return 1;
		}
	}
}
