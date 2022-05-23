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

    default boolean getEnergyLogic() {
        return false;
    }

    default void setEnergyLogic(boolean logic) {}

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
