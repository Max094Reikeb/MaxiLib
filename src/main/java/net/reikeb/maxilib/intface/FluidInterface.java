package net.reikeb.maxilib.intface;

public interface FluidInterface {

    int getWaterLevel();

    void setWaterLevel(int amount);

    int getMaxCapacity();

    default boolean getLogic() {
        return false;
    }

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

    /**
     * Small method to get the amount of fluid in a BlockEntity
     *
     * @param blockEntity The BlockEntity to check
     * @return The amount of fluid
     */
    static <T extends FluidInterface> int getFluidAmount(T blockEntity) {
        return blockEntity.getWaterLevel();
    }

    /**
     * Small method to get the capacity of a tank in a BlockEntity
     *
     * @param blockEntity The BlockEntity to check
     * @return The tank capacity
     */
    static <T extends FluidInterface> int getTankCapacity(T blockEntity) {
        return blockEntity.getMaxCapacity();
    }
}
