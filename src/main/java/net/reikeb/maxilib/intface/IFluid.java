package net.reikeb.maxilib.intface;

/**
 * This class's methods have been moved to {@link FluidInterface}
 */
@Deprecated(forRemoval = true, since = "r1.1")
public class IFluid {

    /**
     * Small method to drain water from a BlockEntity
     *
     * @param blockEntity The BlockEntity we drain water from
     * @param amount      The amount of water drained
     * @deprecated use {@link FluidInterface#drainWater} instead.
     */
    @Deprecated(forRemoval = true, since = "r1.1")
    public static <T extends FluidInterface> void drainWater(T blockEntity, int amount) {
        blockEntity.setWaterLevel(blockEntity.getWaterLevel() - amount);
    }

    /**
     * Small method to fill water into a BlockEntity
     *
     * @param blockEntity The BlockEntity we give water to
     * @param amount      The amount of water given
     * @deprecated use {@link FluidInterface#fillWater} instead.
     */
    @Deprecated(forRemoval = true, since = "r1.1")
    public static <T extends FluidInterface> void fillWater(T blockEntity, int amount) {
        blockEntity.setWaterLevel(blockEntity.getWaterLevel() + amount);
    }

    /**
     * Small method to get the amount of fluid in a BlockEntity
     *
     * @param blockEntity The BlockEntity to check
     * @return The amount of fluid
     * @deprecated use {@link FluidInterface#getWaterLevel} instead.
     */
    @Deprecated(forRemoval = true, since = "r1.1")
    public static <T extends FluidInterface> int getFluidAmount(T blockEntity) {
        return blockEntity.getWaterLevel();
    }

    /**
     * Small method to get the capacity of a tank in a BlockEntity
     *
     * @param blockEntity The BlockEntity to check
     * @return The tank capacity
     * @deprecated use {@link FluidInterface#getTankCapacity} instead.
     */
    @Deprecated(forRemoval = true, since = "r1.1")
    public static <T extends FluidInterface> int getTankCapacity(T blockEntity) {
        return blockEntity.getMaxCapacity();
    }
}
