package com.waterwolfies.js_bot.imixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public interface IWorldMixin {
    
    default void moveBlockEntity(BlockPos pos, BlockPos newPos) {

    }
    default void moveBlockEntity(BlockEntity be, BlockPos newPos) {
    }
}
