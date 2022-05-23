package net.reikeb.maxilib.abs;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.reikeb.maxilib.intface.EnergyInterface;
import net.reikeb.maxilib.inventory.ItemHandler;

public abstract class AbstractEnergyBlockEntity extends AbstractBlockEntity implements EnergyInterface {

    private double electronicPower;
    private int maxStorage;

    protected AbstractEnergyBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, String defaultName, String modId, int slots) {
        super(blockEntityType, pos, state, defaultName, modId, slots);
    }

    public ItemHandler getItemInventory() {
        return this.inventory;
    }

    public void setHundredEnergy(int hundredEnergy) {
        this.electronicPower = hundredEnergy / 100.0;
    }

    public double getEnergy() {
        return this.electronicPower;
    }

    public void setEnergy(double energy) {
        this.electronicPower = energy;
    }

    public int getMaxStorage() {
        return this.maxStorage;
    }

    public void setMaxStorage(int maxStorage) {
        this.maxStorage = maxStorage;
    }

    public void load(CompoundTag compound) {
        super.load(compound);
        this.electronicPower = compound.getDouble("ElectronicPower");
        this.maxStorage = compound.getInt("MaxStorage");
    }

    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putDouble("ElectronicPower", this.electronicPower);
        compound.putInt("MaxStorage", this.maxStorage);
    }
}

