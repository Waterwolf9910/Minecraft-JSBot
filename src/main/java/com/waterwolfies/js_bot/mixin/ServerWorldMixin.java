package com.waterwolfies.js_bot.mixin;

import com.waterwolfies.js_bot.JSBot;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executor;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.Spawner;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    
    @Inject(method = "<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/storage/LevelStorage$Session;Lnet/minecraft/world/level/ServerWorldProperties;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/world/dimension/DimensionOptions;Lnet/minecraft/server/WorldGenerationProgressListener;ZJLjava/util/List;ZLnet/minecraft/util/math/random/RandomSequencesState;)V", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void getWorld(MinecraftServer server, Executor e, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> wk, DimensionOptions doo, WorldGenerationProgressListener w, boolean d, long s, List<Spawner> sp, boolean stt, @Nullable RandomSequencesState rss, CallbackInfo info) {
        setupGlobalScripts(session.getDirectory(WorldSavePath.ROOT).toAbsolutePath().normalize());
    }
    
    @Unique
    public void setupGlobalScripts(Path directory) {
        if (last_path != null && directory.toString().equals(last_path.toString())) {
            return;
        }
        
        Path global_dir = directory.resolve("js_scripts");
        global_dir.toFile().mkdirs();
        JSBot.LOGGER.info("World Global Scripts {}", global_dir);
        last_path = directory;
        JSBot.global_scripts = global_dir;
        JSBot.base_computer_dir = directory.resolve("computer_files");
        JSBot.base_computer_dir.toFile().mkdirs();
    }

    @Unique
    private static Path last_path;
    
}
