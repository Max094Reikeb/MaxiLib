package net.reikeb.maxilib.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;

/**
 * This class is a new concept of custom gravity, opposed to Minecraft's gravity.
 * <p>
 * Public methods are meant to be normally called by modders in order to apply this custom gravity system to a block.
 * Private methods are used by public methods to determine whether a block can be affected by the custom gravity system.
 */
public class Gravity {

    /**
     * Applies gravity to a block
     *
     * @param level The level of the block
     * @param pos   The position of the block
     */
    public static void applyGravity(Level level, BlockPos pos) {
        if (isGravityAffected(level, pos)) {
            FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, level.getBlockState(pos));
            fallingBlockEntity.time = 1;
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            level.addFreshEntity(fallingBlockEntity);
        }
    }

    /**
     * Checks if a block can be moved by gravity
     *
     * @param level The level of the block
     * @param pos   The position of the block
     * @return true if the block can be moved by gravity
     */
    public static boolean isGravityAffected(Level level, BlockPos pos) {
        if (level == null) return false;
        Block block = level.getBlockState(pos).getBlock();
        boolean flag1 = isAir(level, pos);
        boolean flag2 = (block instanceof LeavesBlock) || (block instanceof TorchBlock) || (block instanceof LeverBlock) || (block == Blocks.BEDROCK) || (block instanceof LiquidBlock);
        boolean flag3 = level.isEmptyBlock(pos.below()) || FallingBlock.isFree(level.getBlockState(pos.below()));
        boolean flag4 = isSupport(level, pos);
        boolean flag5 = staysAttached(level, pos);
        boolean flag6 = isAttachedToNormalBlock(level, pos, true);
        return ((!flag1) && (!flag2) && (pos.getY() > -64) && flag3 && (!flag4) && (!flag5) && (!flag6));
    }

    /**
     * Checks if a block is a support (Stairs or Slabs)
     *
     * @param level The level of the block
     * @param pos   The position of the block
     * @return true if the block is considered as a support
     */
    private static boolean isSupport(Level level, BlockPos pos) {
        if (level == null) return false;
        Block block = level.getBlockState(pos).getBlock();
        if ((block instanceof StairBlock) || (block instanceof SlabBlock)) {
            return areBlocksAround(level, pos);
        }
        return false;
    }

    /**
     * Checks if a block can stay attached to another block
     *
     * @param level The level of the block
     * @param pos   The position of the block
     * @return true if the block can stay attached
     */
    private static boolean staysAttached(Level level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        if ((block instanceof FenceBlock) || (block instanceof FenceGateBlock) || (block instanceof ChainBlock) || (block instanceof IronBarsBlock)) {
            return areBlocksAround(level, pos);
        }
        return false;
    }

    /**
     * Checks if blocks are around a position
     *
     * @param level The level of the blocks
     * @param pos   The position of base block
     * @return true if blocks are around the position
     */
    private static boolean areBlocksAround(Level level, BlockPos pos) {
        return !isAir(level, pos.north()) || !isAir(level, pos.south()) || !isAir(level, pos.east()) || !isAir(level, pos.west());
    }

    /**
     * Checks if a block is air
     *
     * @param level The level of the block
     * @param pos   The position of the block
     * @return true if the block is air
     */
    public static boolean isAir(Level level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        return (block == Blocks.AIR) || (block == Blocks.VOID_AIR) || (block == Blocks.CAVE_AIR);
    }

    /**
     * Checks if a block is attached to a normal block over 1 distance
     *
     * @param level          The level of the block
     * @param pos            The position of the block
     * @param checkNextBlock Whether it should check for a next block
     * @return true if attached to a normal block over 1 distance
     */
    private static boolean isAttachedToNormalBlock(Level level, BlockPos pos, boolean checkNextBlock) {
        for (Direction dir : Direction.values()) {
            BlockPos otherPos = pos.relative(dir);
            if ((!isSupport(level, otherPos)) && (!staysAttached(level, otherPos))) {
                return true;
            } else {
                if (checkNextBlock) {
                    if (!isAir(level, otherPos)) {
                        return isAttachedToNormalBlock(level, otherPos, false);
                    }
                }
            }
            return false;
        }
        return false;
    }
}