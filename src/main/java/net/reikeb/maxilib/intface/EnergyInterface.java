package net.reikeb.maxilib.intface;

import net.reikeb.maxilib.inventory.ItemHandler;

public interface EnergyInterface {

    ItemHandler getItemInventory();

    double getEnergy();

    void setEnergy(double energy);

    default int getHundredEnergy() {
        return (int) getEnergy() * 100;
    }

    void setHundredEnergy(int hundredEnergy);

    int getMaxEnergy();

    void setMaxEnergy(int maxEnergy);

    /**
     * @deprecated use {@link #getHundredEnergy} instead.
     */
    @Deprecated(forRemoval = true, since = "r1.1")
    int getElectronicPowerTimesHundred();

    /**
     * @deprecated use {@link #setHundredEnergy} instead.
     */
    @Deprecated(forRemoval = true, since = "r1.1")
    default void setElectronicPowerTimesHundred(int electronicPowerTimesHundred) {}

    /**
     * @deprecated use {@link #getEnergy} instead.
     */
    @Deprecated(forRemoval = true, since = "r1.1")
    double getElectronicPower();

    /**
     * @deprecated use {@link #setEnergy} instead.
     */
    @Deprecated(forRemoval = true, since = "r1.1")
    default void setElectronicPower(double electronicPower) {}

    /**
     * @deprecated use {@link #getMaxEnergy} instead.
     */
    @Deprecated(forRemoval = true, since = "r1.1")
    int getMaxStorage();

    /**
     * @deprecated use {@link #setMaxEnergy} instead.
     */
    @Deprecated(forRemoval = true, since = "r1.1")
    void setMaxStorage(int maxStorage);

    default boolean getEnergyLogic() {
        return false;
    }

    default void setEnergyLogic(boolean logic) {}

    /**
     * @deprecated use {@link #getEnergyLogic} instead.
     */
    @Deprecated(forRemoval = true, since = "r1.1")
    default boolean getLogic() {
        return false;
    }

    /**
     * @deprecated use {@link #setEnergyLogic} instead.
     */
    @Deprecated(forRemoval = true, since = "r1.1")
    default void setLogic(boolean logic) {}

    /**
     * Small method to drain energy from a BlockEntity
     *
     * @param blockEntity The BlockEntity we drain energy from
     * @param amount      The amount of energy drained
     */
    static <T extends EnergyInterface> void drainEnergy(T blockEntity, double amount) {
        blockEntity.setEnergy(blockEntity.getEnergy() - amount);
    }

    /**
     * Small method to fill energy into a BlockEntity
     *
     * @param blockEntity The BlockEntity we give energy to
     * @param amount      The amount of energy given
     */
    static <T extends EnergyInterface> void fillEnergy(T blockEntity, double amount) {
        blockEntity.setEnergy(blockEntity.getEnergy() + amount);
    }
}
