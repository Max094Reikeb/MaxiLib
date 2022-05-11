package net.reikeb.maxilib.intface;

public interface FluidInterface {

    int getWaterLevel();

    void setWaterLevel(int amount);

    int getMaxCapacity();

    boolean getLogic();

    void setLogic(boolean logic);
}
