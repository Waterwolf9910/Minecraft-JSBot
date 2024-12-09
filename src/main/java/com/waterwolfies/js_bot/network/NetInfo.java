package com.waterwolfies.js_bot.network;

import com.waterwolfies.js_bot.JSBot;

import net.minecraft.util.Identifier;

/**
 * Send S2C with ServerPlayNetworking
 * <p>
 * Send C2S with ClientPlayNetworking
 * <p>
 * Buffers are sent with PacketByteBuf
 * <p>
 * {@see net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking#registerGlobalReceiver}
 */
// TODO: Prevent path traversal possibility and accessed computer id
public class NetInfo {
    
    public static class C2S {
        /**
         * (int) ComputerID
         * <p>
         * (String) File Name
         * <p>
         * (BlockPos) pos
         */
        public static final Identifier RUN_JS_CODE = Identifier.of(JSBot.MOD_ID, "run_js_code");
        /**
         * (int) ComputerID
         * <p>
         * (String) File name
         * <p>
         * (String) file_data
         */
        public static final Identifier SAVE_JS_FILE = Identifier.of(JSBot.MOD_ID, "save_js_code");
        /**
         * (int) ComputerID
         * <p>
         */
        public static final Identifier GRAB_FILE_LIST = Identifier.of(JSBot.MOD_ID, "");
        /**
         * (int) ComputerID
         * (String) file_name
         */
        public static final Identifier LOAD_FILE = Identifier.of(JSBot.MOD_ID, "load_file");
    }

    public static class S2C {
        /**
         * (int) ComputerID
         * <p>
         * (String) [timestamp] message
         */
        public static final Identifier LOG = Identifier.of(JSBot.MOD_ID, "log");
        /**
         * (int) Computer ID
         * (int) file_name_count
         * (String ...) file_names
         */
        public static final Identifier FILE_LIST = Identifier.of(JSBot.MOD_ID, "file_list");
        /**
         * (int) ComputerID
         * (String) file_data
         */
        public static final Identifier FILE_DATA = Identifier.of(JSBot.MOD_ID, "file_data");
        /**
         * (int) ComputerID
         * 
         */
        public static final Identifier RUN_COMPLETED = Identifier.of(JSBot.MOD_ID, "run_complete");
    }
}
