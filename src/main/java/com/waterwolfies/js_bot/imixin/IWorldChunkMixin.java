package com.waterwolfies.js_bot.imixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public interface IWorldChunkMixin {
    
    default void moveBlockEntity(BlockEntity init, BlockPos newPos) {
    }

}
