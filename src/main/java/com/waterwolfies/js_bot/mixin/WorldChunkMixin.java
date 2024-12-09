package com.waterwolfies.js_bot.mixin;

import java.lang.reflect.Field;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;

@Mixin({WorldChunk.class})
public abstract class WorldChunkMixin {

    @Shadow
    protected abstract void updateGameEventListener(BlockEntity be, ServerWorld world);

    @Shadow
    protected abstract void removeGameEventListener(BlockEntity blockEntity, ServerWorld world);

    @Shadow
    protected abstract void updateTicker(BlockEntity be);

    @Shadow
    protected abstract void removeBlockEntityTicker(BlockPos pos);

    @Unique
    public void moveBlockEntity(BlockEntity init, BlockPos newPos) {
        if (init == null) {
            return;
        }
        BlockState state = init.getCachedState();
        init.getWorld().setBlockState(init.getPos(), Blocks.AIR.getDefaultState(), Block.MOVED);
        ((ChunkIMixin) this).getBlockEntityMap().remove(init.getPos());
        if (init.getWorld() instanceof ServerWorld world) {
            this.removeGameEventListener(init, world);
        }
        this.removeBlockEntityTicker(init.getPos());
        init.getWorld().setBlockState(newPos, state);
        try {
            Field field = init.getClass().getField("pos");
            field.setAccessible(true);
            field.set(init, newPos.toImmutable());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        WorldChunkMixin newChunk = ((WorldChunkMixin) (Object) init.getWorld().getChunk(newPos));
        ((ChunkIMixin) newChunk).getBlockEntityMap().put(newPos.toImmutable(), init);
        if (init.getWorld() instanceof ServerWorld world) {
            newChunk.updateGameEventListener(init, world);
        }
        newChunk.updateTicker(init);
    }
}
