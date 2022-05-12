package net.reikeb.maxilib;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;

public class RInputs {

    private final ItemStack input1;
    private final ItemStack input2;

    public RInputs(Block block1, Block block2) {
        this(new ItemStack(block1.asItem()), new ItemStack(block2.asItem()));
    }

    public RInputs(Item input1, Item input2) {
        this(new ItemStack(input1), new ItemStack(input2));
    }

    public RInputs(ItemStack input1, Item input2) {
        this(input1, new ItemStack(input2));
    }

    public RInputs(Item input1, ItemStack input2) {
        this(new ItemStack(input1), input2);
    }

    public RInputs(ItemStack input1, ItemStack input2) {
        this.input1 = input1;
        this.input2 = input2;
    }

    public final ItemStack getInput1() {
        return this.input1;
    }

    public final Item getItemInput1() {
        return this.input1.getItem();
    }

    public final ItemStack getInput2() {
        return this.input2;
    }

    public final Item getItemInput2() {
        return this.input2.getItem();
    }

    public final ArrayList<ItemStack> getInputs() {
        ArrayList<ItemStack> inputs = new ArrayList<>();
        inputs.add(this.input1);
        inputs.add(this.input2);
        return inputs;
    }

    /**
     * Compares this RInput to the specified object. The result is {@code
     * true} if and only if the argument is not {@code null} and is a {@code
     * RInput} object that represents the same RInput as this object.
     *
     * @param anObject The object to compare this {@code RInput} against
     * @return {@code true} if the given object represents a {@code RInput}
     * equivalent to this RInput, {@code false} otherwise
     */
    public boolean equalsTo(Object anObject) {
        if (this == anObject) {
            return true;
        }
        return anObject instanceof RInputs && ((RInputs) anObject).input1.equals(this.input1) && ((RInputs) anObject).input2.equals(this.input2);
    }
}