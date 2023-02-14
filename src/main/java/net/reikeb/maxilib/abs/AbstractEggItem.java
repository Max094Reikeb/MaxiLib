package net.reikeb.maxilib.abs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.Objects;
import java.util.function.Supplier;

public class AbstractEggItem extends ForgeSpawnEggItem {

    private final Supplier<? extends EntityType<? extends Mob>> entityType;

    public AbstractEggItem(Supplier<? extends EntityType<? extends Mob>> entityType, int color1, int color2) {
        super(entityType, color1, color2, new Properties().stacksTo(1));
        this.entityType = entityType;
        DispenserBlock.registerBehavior(this,
                new DefaultDispenseItemBehavior() {
                    public ItemStack dispenseStack(BlockSource source, ItemStack stack) {
                        Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
                        EntityType<?> entityType = ((SpawnEggItem) stack.getItem()).getType(stack.getTag());
                        entityType.spawn(source.getLevel(), stack, null, source.getPos().relative(direction), MobSpawnType.DISPENSER, direction != Direction.UP, false);
                        stack.shrink(1);
                        return stack;
                    }
                });
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) return InteractionResult.PASS;
        ItemStack itemStack = context.getItemInHand();
        BlockPos blockPos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockState blockState = level.getBlockState(blockPos);
        if (level.getBlockEntity(blockPos) instanceof SpawnerBlockEntity spawnerBlockEntity) {
            BaseSpawner baseSpawner = spawnerBlockEntity.getSpawner();
            baseSpawner.setEntityId(this.entityType.get(), level, RandomSource.create(), blockPos);
            spawnerBlockEntity.setChanged();
            level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_CLIENTS + Block.UPDATE_NEIGHBORS);
            itemStack.shrink(1);
            return InteractionResult.SUCCESS;
        }
        BlockPos blockpos1;
        if (blockState.getCollisionShape(level, blockPos).isEmpty()) {
            blockpos1 = blockPos;
        } else {
            blockpos1 = blockPos.relative(direction);
        }

        if (this.entityType.get().spawn((ServerLevel) level, itemStack, context.getPlayer(), blockpos1, MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockPos, blockpos1) && direction == Direction.UP) != null) {
            itemStack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}