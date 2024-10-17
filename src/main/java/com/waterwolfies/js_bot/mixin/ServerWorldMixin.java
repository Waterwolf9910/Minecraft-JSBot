package com.waterwolfies.js_bot.mixin;

import com.waterwolfies.js_bot.JSBot;

import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
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
    
    @Inject(method = "<init>()V", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
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
    }

    @Unique
    private static Path last_path;
    
}
