package net.reikeb.maxilib.intface;

public class IEnergy {

    /**
     * Small method to drain energy from a BlockEntity
     *
     * @param blockEntity The BlockEntity we drain energy from
     * @param amount      The amount of energy drained
     */
    public static <T extends EnergyInterface> void drainEnergy(T blockEntity, double amount) {
        blockEntity.setElectronicPower(blockEntity.getElectronicPower() - amount);
    }

    /**
     * Small method to fill energy into a BlockEntity
     *
     * @param blockEntity The BlockEntity we give energy to
     * @param amount      The amount of energy given
     */
    public static <T extends EnergyInterface> void fillEnergy(T blockEntity, double amount) {
        blockEntity.setElectronicPower(blockEntity.getElectronicPower() + amount);
    }

    /**
     * Small method to get the energy of a block in a BlockEntity
     *
     * @param blockEntity The BlockEntity to check
     * @return The amount of energy
     */
    public static <T extends EnergyInterface> double getEnergy(T blockEntity) {
        return blockEntity.getElectronicPower();
    }

    /**
     * Small method to set the energy of a BlockEntity
     *
     * @param blockEntity The BlockEntity we set energy to
     * @param amount      The amount of energy we set
     */
    public static <T extends EnergyInterface> void setEnergy(T blockEntity, double amount) {
        blockEntity.setElectronicPower(amount);
    }

    /**
     * Small method to get the capacity of a BlockEntity
     *
     * @param blockEntity The BlockEntity to check
     * @return The capacity
     */
    public static <T extends EnergyInterface> int getMaxEnergy(T blockEntity) {
        return blockEntity.getMaxStorage();
    }
}
