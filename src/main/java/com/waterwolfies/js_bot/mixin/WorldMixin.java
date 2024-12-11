package com.waterwolfies.js_bot.mixin;

import com.waterwolfies.js_bot.imixin.IWorldMixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(World.class)
public abstract class WorldMixin implements IWorldMixin {
    
    @Shadow
    public abstract WorldChunk getWorldChunk(BlockPos pos);

    @Shadow
    public abstract BlockEntity getBlockEntity(BlockPos pos);

    @Shadow
    public abstract boolean isInBuildLimit(BlockPos pos);

    @Override
    public void moveBlockEntity(BlockPos pos, BlockPos newPos) {
        if (isInBuildLimit(pos) || isInBuildLimit(newPos)) {
            return;
        }
        BlockEntity be = this.getBlockEntity(pos);
        ((WorldChunkMixin) (Object) this.getWorldChunk(pos)).moveBlockEntity(be, newPos);
    }

    @Override
    public void moveBlockEntity(BlockEntity be, BlockPos newPos) {
        if (isInBuildLimit(newPos)) {
            return;
        }
        ((WorldChunkMixin) (Object) this.getWorldChunk(be.getPos())).moveBlockEntity(be, newPos);
    }
}
