package net.reikeb.maxilib.abs;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public abstract class AbstractContainer extends AbstractContainerMenu {

    private final int slotCount;

    public AbstractContainer(MenuType<?> type, int id, int slotCount) {
        super(type, id);
        this.slotCount = slotCount;
    }

    @Override
    public boolean stillValid(@NotNull Player playerEntity) {
        return true;
    }

    public void layoutPlayerInventorySlots(Inventory playerInv) {
        int si;
        int sj;
        for (si = 0; si < 3; ++si)
            for (sj = 0; sj < 9; ++sj)
                this.addSlot(new Slot(playerInv, sj + (si + 1) * 9, 8 + sj * 18, 84 + si * 18));
        for (si = 0; si < 9; ++si)
            this.addSlot(new Slot(playerInv, si, 8 + si * 18, 142));
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.slotCount) {
                if (!this.moveItemStackTo(itemstack1, this.slotCount, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (!this.moveItemStackTo(itemstack1, 0, this.slotCount, false)) {
                if (index < this.slotCount + 27) {
                    if (!this.moveItemStackTo(itemstack1, this.slotCount + 27, this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(itemstack1, this.slotCount, this.slotCount + 27, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                return ItemStack.EMPTY;
            }
            if (itemstack1.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemstack1);
        }
        return itemstack;
    }

    public void addSyncedInt(IntConsumer intConsumer, IntSupplier intSupplier) {
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return intSupplier.getAsInt() & 0xffff;
            }

            @Override
            public void set(int value) {
                int stored = intSupplier.getAsInt() & 0xffff0000;
                intConsumer.accept(stored + (value & 0xffff));
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (intSupplier.getAsInt() >> 16) & 0xffff;
            }

            @Override
            public void set(int value) {
                int stored = intSupplier.getAsInt() & 0x0000ffff;
                intConsumer.accept(stored | (value << 16));
            }
        });
    }
}
