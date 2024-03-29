package net.reikeb.maxilib.abs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.reikeb.maxilib.inventory.ItemHandler;

import java.util.Objects;

public abstract class AbstractBlockEntity extends BaseContainerBlockEntity {

    public final ItemHandler inventory;
    public int slots;
    private final String defaultName;
    private final String modId;

    protected AbstractBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, String defaultName, String modId, int slots) {
        super(blockEntityType, pos, state);

        this.slots = slots;
        this.inventory = new ItemHandler(slots);
        this.defaultName = defaultName;
        this.modId = modId;
    }

    @Override
    protected Component getDefaultName() {
        return Component.literal(this.defaultName);
    }

    private Component getModId() {
        return Component.literal(this.modId);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui." + this.getModId() + "." + this.getDefaultName() + ".name");
    }

    public ItemStack getItem(int slot) {
        return this.inventory.getStackInSlot(slot);
    }

    public ItemStack removeItem(int index, int count) {
        return this.inventory.extractItem(index, count, false);
    }

    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = this.inventory.getStackInSlot(index);
        this.inventory.removeStackFromSlot(index);
        return stack;
    }

    public void setItem(int slot, ItemStack itemStack) {
        this.inventory.setStackInSlot(slot, itemStack);
        if (itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }
    }

    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    public void clearContent() {
        this.inventory.clear();
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return null;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
    }

    public final IItemHandlerModifiable getInventory() {
        return this.inventory;
    }

    public void removeItemIndexCount(int index, int count) {
        this.inventory.decrStackSize(index, count);
    }

    public void setItemIndexCount(int index, int count, Item item) {
        this.inventory.setStackInSlot(index, new ItemStack(item, count));
    }

    public void setStackIndex(int index, ItemStack stack) {
        this.inventory.setStackInSlot(index, stack);
    }

    @Override
    public void load(CompoundTag compoundNBT) {
        super.load(compoundNBT);
        if (compoundNBT.contains("Inventory")) {
            inventory.deserializeNBT((CompoundTag) compoundNBT.get("Inventory"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag compoundNBT) {
        super.saveAdditional(compoundNBT);
        compoundNBT.put("Inventory", inventory.serializeNBT());
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, LazyOptional.of(() -> this.inventory));
    }

    public void dropItems(Level level, BlockPos pos) {
        for (int i = 0; i < slots; i++)
            if (!inventory.getStackInSlot(i).isEmpty()) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(i));
            }
    }

    public boolean isEmpty() {
        return this.inventory.isEmpty();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(Objects.requireNonNull(pkt.getTag()));
    }

    @Override
    public int getContainerSize() {
        return slots;
    }
}
