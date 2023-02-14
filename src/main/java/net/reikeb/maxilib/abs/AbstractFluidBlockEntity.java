package net.reikeb.maxilib.abs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.reikeb.maxilib.intface.FluidInterface;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public abstract class AbstractFluidBlockEntity extends AbstractBlockEntity implements FluidInterface {

    private final int fluidCapacity;
    private final FluidTankHandler fluidTank;

    protected AbstractFluidBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, String defaultName, String modId, int slots, int fluidCapacity) {
        super(blockEntityType, pos, state, defaultName, modId, slots);

        this.fluidCapacity = fluidCapacity;
        this.fluidTank = new FluidTankHandler(this.fluidCapacity, fs -> {
            return fs.getFluid() == Fluids.WATER;
        }) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                setChanged();
                if (level == null) return;
                level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
            }
        };
    }

    public int getWaterLevel() {
        AtomicInteger amount = new AtomicInteger();
        this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
                .ifPresent(cap -> amount.set(cap.getFluidInTank(1).getAmount()));
        return amount.get();
    }

    public void setWaterLevel(int amount) {
        this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
                .ifPresent(cap -> cap.drain(getWaterLevel(), IFluidHandler.FluidAction.EXECUTE));
        this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
                .ifPresent(cap -> cap.fill(new FluidStack(Fluids.WATER, amount), IFluidHandler.FluidAction.EXECUTE));
    }

    public int getTankCapacity() {
        AtomicInteger capacity = new AtomicInteger();
        this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
                .ifPresent(cap -> capacity.set(cap.getTankCapacity(1)));
        return capacity.get();
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (compound.get("fluidTank") != null) {
            fluidTank.readFromNBT((CompoundTag) compound.get("fluidTank"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("fluidTank", fluidTank.serializeNBT());
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return LazyOptional.of(() -> fluidTank).cast();
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> this.inventory));
    }

    public static class FluidTankHandler extends FluidTank {
        public FluidTankHandler(int capacity, Predicate<FluidStack> validator) {
            super(capacity, validator);
        }

        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("FluidName", this.fluid.getFluid().toString());
            nbt.putInt("Amount", this.fluid.getAmount());
            return nbt;
        }
    }
}

