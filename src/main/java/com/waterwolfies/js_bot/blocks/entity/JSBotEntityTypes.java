package com.waterwolfies.js_bot.blocks.entity;

import com.waterwolfies.js_bot.JSBot;

import com.mojang.datafixers.types.constant.EmptyPart;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class JSBotEntityTypes {
    public static <T extends BlockEntityType<?>> T register(String path, T block_entity_type) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(JSBot.MOD_ID, path), block_entity_type);
    }

    public static final BlockEntityType<JamesBlockEntity> JAMES_BLOCK_ENTITY_TYPE = register(
        "james_block",
        // For versions 1.21.2 and above,
        // replace `BlockEntityType.Builder` with `FabricBlockEntityTypeBuilder`.
        BlockEntityType.Builder.create(JamesBlockEntity::new, JSBot.JAMES_BLOCK).build(new EmptyPart())
    );

    public static void initialize() {
    }
}
