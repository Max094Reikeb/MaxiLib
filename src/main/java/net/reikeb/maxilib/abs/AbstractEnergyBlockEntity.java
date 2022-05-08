package net.reikeb.maxilib.abs;

import net.reikeb.maxilib.inventory.ItemHandler;

public interface AbstractEnergyBlockEntity {

    ItemHandler getItemInventory();

    int getElectronicPowerTimesHundred();

    void setElectronicPowerTimesHundred(int electronicPowerTimesHundred);

    double getElectronicPower();

    void setElectronicPower(double electronicPower);

    int getMaxStorage();

    void setMaxStorage(int maxStorage);

    boolean getLogic();

    void setLogic(boolean logic);
}
