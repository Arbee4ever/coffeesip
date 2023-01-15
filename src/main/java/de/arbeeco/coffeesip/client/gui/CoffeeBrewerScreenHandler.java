package de.arbeeco.coffeesip.client.gui;

import de.arbeeco.coffeesip.registries.CoffeeItems;
import de.arbeeco.coffeesip.registries.CoffeeScreens;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public class CoffeeBrewerScreenHandler extends ScreenHandler {
	private static final int BOTTLE_SLOTS_START = 0;
	private static final int BOTTLE_SLOTS_END = 2;
	private static final int INGREDIENT_SLOT = 3;
	private static final int FUEL_SLOT = 4;
	private static final int SLOTS_COUNT = 5;
	private static final int PROPERTIES_COUNT = 2;
	private static final int INVENTORY_SLOTS_START = 5;
	private static final int INVENTORY_SLOTS_END = 32;
	private static final int HOTBAR_SLOTS_START = 32;
	private static final int HOTBAR_SLOTS_END = 41;
	private final Inventory inventory;
	private final PropertyDelegate propertyDelegate;
	private final Slot ingredientSlot;
	public CoffeeBrewerScreenHandler(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, new SimpleInventory(5), new ArrayPropertyDelegate(2));
	}

	public CoffeeBrewerScreenHandler(int i, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(ScreenHandlerType.BREWING_STAND, i);
		checkSize(inventory, 5);
		checkDataCount(propertyDelegate, 2);
		this.inventory = inventory;
		this.propertyDelegate = propertyDelegate;
		addSlot(new CupSlot(inventory, 0, 56, 51));
		addSlot(new CupSlot(inventory, 1, 79, 58));
		addSlot(new CupSlot(inventory, 2, 102, 51));
		ingredientSlot = addSlot(new IngredientSlot(inventory, 3, 79, 17));
		addSlot(new FuelSlot(inventory, 4, 17, 17));
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
					if (insertItem(itemStack2, 4, 5, false) || ingredientSlot.canInsert(itemStack2) && !insertItem(itemStack2, 3, 4, false)) {
						return ItemStack.EMPTY;
					}
				} else if (ingredientSlot.canInsert(itemStack2)) {
					if (!insertItem(itemStack2, 3, 4, false)) {
						return ItemStack.EMPTY;
					}
				} else if (CupSlot.matches(itemStack) && itemStack.getCount() == 1) {
					if (!insertItem(itemStack2, 0, 3, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 5 && index < 32) {
					if (!insertItem(itemStack2, 32, 41, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 32 && index < 41) {
					if (!insertItem(itemStack2, 5, 32, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!insertItem(itemStack2, 5, 41, false)) {
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

	public int getFuel() {
		return propertyDelegate.get(1);
	}

	public int getBrewTime() {
		return propertyDelegate.get(0);
	}

	static class CupSlot extends Slot {
		public CupSlot(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		public boolean canInsert(ItemStack stack) {
			return matches(stack);
		}

		public int getMaxItemCount() {
			return 1;
		}

		public void onTakeItem(PlayerEntity player, ItemStack stack) {
			Potion potion = PotionUtil.getPotion(stack);
			if (player instanceof ServerPlayerEntity) {
				Criteria.BREWED_POTION.trigger((ServerPlayerEntity)player, potion);
			}

			super.onTakeItem(player, stack);
		}

		public static boolean matches(ItemStack stack) {
			return stack.isOf(CoffeeItems.COFFEE_CUP) || stack.isOf(CoffeeItems.CUP);
		}
	}

	private static class IngredientSlot extends Slot {
		public IngredientSlot(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		public boolean canInsert(ItemStack stack) {
			return stack.isOf(CoffeeItems.COFFEE_BEANS);
		}

		public int getMaxItemCount() {
			return 64;
		}
	}

	private static class FuelSlot extends Slot {
		public FuelSlot(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		public boolean canInsert(ItemStack stack) {
			return matches(stack);
		}

		public static boolean matches(ItemStack stack) {
			return stack.isOf(Items.COAL);
		}

		public int getMaxItemCount() {
			return 64;
		}
	}
}
