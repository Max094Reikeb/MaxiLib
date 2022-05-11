package net.reikeb.maxilib.intface;

import net.reikeb.maxilib.abs.AbstractEnergyBlockEntity;

public class IEnergy {

    /**
     * Small method to drain energy from a BlockEntity
     *
     * @param be     The BlockEntity we drain energy from
     * @param amount The amount of energy drained
     */
    public static void drainEnergy(AbstractEnergyBlockEntity be, double amount) {
        be.setElectronicPower(be.getElectronicPower() - amount);
    }

    /**
     * Small method to fill energy into a BlockEntity
     *
     * @param be     The BlockEntity we give energy to
     * @param amount The amount of energy given
     */
    public static void fillEnergy(AbstractEnergyBlockEntity be, double amount) {
        be.setElectronicPower(be.getElectronicPower() + amount);
    }

    /**
     * Small method to get the energy of a block in a BlockEntity
     *
     * @param be The BlockEntity to check
     * @return The amount of energy
     */
    public static double getEnergy(AbstractEnergyBlockEntity be) {
        return be.getElectronicPower();
    }

    /**
     * Small method to set the energy of a BlockEntity
     *
     * @param be     The BlockEntity we set energy to
     * @param amount The amount of energy we set
     */
    public static void setEnergy(AbstractEnergyBlockEntity be, double amount) {
        be.setElectronicPower(amount);
    }

    /**
     * Small method to get the capacity of a BlockEntity
     *
     * @param be The BlockEntity to check
     * @return The capacity
     */
    public static int getMaxEnergy(AbstractEnergyBlockEntity be) {
        return be.getMaxStorage();
    }
}
