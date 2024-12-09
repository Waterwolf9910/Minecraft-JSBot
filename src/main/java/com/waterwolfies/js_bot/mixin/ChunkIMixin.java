package com.waterwolfies.js_bot.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

@Mixin(Chunk.class)
public interface ChunkIMixin {
    
    @Accessor("blockEntities")
    public abstract Map<BlockPos, BlockEntity> getBlockEntityMap();
}
