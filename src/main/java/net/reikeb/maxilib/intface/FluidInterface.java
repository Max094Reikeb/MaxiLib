package net.reikeb.maxilib.intface;

public interface FluidInterface {

    int getWaterLevel();

    void setWaterLevel(int amount);

    int getTankCapacity();

    /**
     * @deprecated use {@link #getTankCapacity} instead.
     */
    @Deprecated(forRemoval = true, since = "r1.1")
    default int getMaxCapacity() {
        return getTankCapacity();
    }

    default boolean getFluidLogic() {
        return false;
    }

    default void setFluidLogic(boolean logic) {}

    /**
     * @deprecated use {@link #getFluidLogic} instead.
     */
    @Deprecated(forRemoval = true, since = "r1.1")
    default boolean getLogic() {
        return false;
    }

    /**
     * @deprecated use {@link #setFluidLogic} instead.
     */
    @Deprecated(forRemoval = true, since = "r1.1")
    default void setLogic(boolean logic) {}

    /**
     * Small method to drain water from a BlockEntity
     *
     * @param blockEntity The BlockEntity we drain water from
     * @param amount      The amount of water drained
     */
    static <T extends FluidInterface> void drainWater(T blockEntity, int amount) {
        blockEntity.setWaterLevel(blockEntity.getWaterLevel() - amount);
    }

    /**
     * Small method to fill water into a BlockEntity
     *
     * @param blockEntity The BlockEntity we give water to
     * @param amount      The amount of water given
     */
    static <T extends FluidInterface> void fillWater(T blockEntity, int amount) {
        blockEntity.setWaterLevel(blockEntity.getWaterLevel() + amount);
    }
}
