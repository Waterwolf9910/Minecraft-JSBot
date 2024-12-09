package com.waterwolfies.js_bot;

import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

// TODO: World (Dimension) based ids
public class JSBotPersistanceState extends PersistentState {
    public AtomicInteger last_computer_id = new AtomicInteger(0);

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("last_computer_id", this.last_computer_id.get());
        return nbt;
    }

    public static JSBotPersistanceState fromNbt(NbtCompound tag) {
        JSBotPersistanceState state = new JSBotPersistanceState();
        state.last_computer_id.set(tag.getInt("last_computer_id"));
        return state;
    }

    public static JSBotPersistanceState getServerState(MinecraftServer server) {
        PersistentStateManager manager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        
        JSBotPersistanceState state = manager.getOrCreate(JSBotPersistanceState::fromNbt, JSBotPersistanceState::new, JSBot.MOD_ID);

        state.markDirty();

        return state;
    }
    
}
