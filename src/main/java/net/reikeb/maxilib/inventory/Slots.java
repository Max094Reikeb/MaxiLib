package net.reikeb.maxilib.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class Slots extends SlotItemHandler {

    private final Predicate<ItemStack> mayPlace;
    private final boolean mayPickup;
    private final int maxStackSize;

    public Slots(IItemHandler itemHandler, int id, int x, int y) {
        this(itemHandler, id, x, y, c -> true, true, 64);
    }

    public Slots(IItemHandler itemHandler, int id, int x, int y, Predicate<ItemStack> mayPlace) {
        this(itemHandler, id, x, y, mayPlace, true, 64);
    }

    public Slots(IItemHandler itemHandler, int id, int x, int y, int maxStackSize) {
        this(itemHandler, id, x, y, c -> true, true, maxStackSize);
    }

    public Slots(IItemHandler itemHandler, int id, int x, int y, Predicate<ItemStack> mayPlace, int maxStackSize) {
        this(itemHandler, id, x, y, mayPlace, true, maxStackSize);
    }

    public Slots(IItemHandler itemHandler, int id, int x, int y, Predicate<ItemStack> mayPlace, boolean mayPickup, int maxStackSize) {
        super(itemHandler, id, x, y);
        this.mayPlace = mayPlace;
        this.mayPickup = mayPickup;
        this.maxStackSize = maxStackSize;
    }

    public boolean mayPlace(@NotNull ItemStack itemStack) {
        return this.mayPlace.test(itemStack);
    }

    public boolean mayPickup(Player player) {
        return this.mayPickup;
    }

    public int getMaxStackSize() {
        return this.maxStackSize;
    }
}
