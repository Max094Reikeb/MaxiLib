package net.reikeb.maxilib.intface;

import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.reikeb.maxilib.abs.AbstractFluidBlockEntity;

import java.util.concurrent.atomic.AtomicInteger;

public class IFluid {

    /**
     * Small method to drain water from a BlockEntity
     *
     * @param be     The BlockEntity we drain water from
     * @param amount The amount of water drained
     */
    public static void drainWater(AbstractFluidBlockEntity be, int amount) {
        be.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
                .ifPresent(cap -> cap.drain(amount, IFluidHandler.FluidAction.EXECUTE));
    }

    /**
     * Small method to fill water into a BlockEntity
     *
     * @param be     The BlockEntity we give water to
     * @param amount The amount of water given
     */
    public static void fillWater(AbstractFluidBlockEntity be, int amount) {
        be.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
                .ifPresent(cap -> cap.fill(new FluidStack(Fluids.WATER, amount), IFluidHandler.FluidAction.EXECUTE));
    }

    /**
     * Small method to get the amount of fluid in a BlockEntity
     *
     * @param be The BlockEntity to check
     * @return The amount of fluid
     */
    public static AtomicInteger getFluidAmount(AbstractFluidBlockEntity be) {
        AtomicInteger amount = new AtomicInteger();
        be.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
                .ifPresent(cap -> amount.set(cap.getFluidInTank(1).getAmount()));
        return amount;
    }

    /**
     * Small method to get the capacity of a tank in a BlockEntity
     *
     * @param be The BlockEntity to check
     * @return The tank capacity
     */
    public static AtomicInteger getTankCapacity(AbstractFluidBlockEntity be) {
        AtomicInteger capacity = new AtomicInteger();
        be.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
                .ifPresent(cap -> capacity.set(cap.getTankCapacity(1)));
        return capacity;
    }
}
