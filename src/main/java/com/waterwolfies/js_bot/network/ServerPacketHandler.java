package com.waterwolfies.js_bot.network;

import com.waterwolfies.js_bot.JSBot;
import com.waterwolfies.js_bot.blocks.entity.JamesBlockEntity;
import com.waterwolfies.js_bot.js.JSApi;
import com.waterwolfies.js_bot.js.JSHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ServerPacketHandler {

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(NetInfo.C2S.RUN_JS_CODE, new runJSCode()::receive);
        ServerPlayNetworking.registerGlobalReceiver(NetInfo.C2S.SAVE_JS_FILE, new saveJSCode()::receive);
        ServerPlayNetworking.registerGlobalReceiver(NetInfo.C2S.GRAB_FILE_LIST, new grabFileList()::receive);
        ServerPlayNetworking.registerGlobalReceiver(NetInfo.C2S.LOAD_FILE, new loadFile()::receive);
    }

    public static class saveJSCode implements ServerPlayNetworking.PlayChannelHandler {
        @Override
        public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            int id = buf.readInt();
            String file_name = buf.readString();
            String file_data = buf.readString();

            Path file = JSBot.base_computer_dir.resolve("computer_" + id + "/" + file_name);
            try {
                if (!Files.exists(file)) {
                    Files.createFile(file);
                }
                Files.writeString(file, file_data);
            } catch (IOException e) {
                PacketByteBuf _buf = PacketByteBufs.create();
                _buf.writeInt(id);
                _buf.writeString("Error Saving File: " + file_name + "\n");
                ServerPlayNetworking.send(player, NetInfo.S2C.LOG, _buf);
                e.printStackTrace();
            }
        }
    }

    public static class runJSCode implements ServerPlayNetworking.PlayChannelHandler {

        @Override
        public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            int id = buf.readInt();
            String file_name = buf.readString();
            BlockPos pos = buf.readBlockPos();
            server.execute(() -> {
                JamesBlockEntity be = (JamesBlockEntity) server.getWorld(World.OVERWORLD).getBlockEntity(pos);
                OutputStream out = new OutputStream() {

                    @Override
                    public void write(int b) throws IOException {
                        PacketByteBuf _buf = PacketByteBufs.create();
                        _buf.writeInt(id);
                        _buf.writeString("" + b);
                        ServerPlayNetworking.send(player, NetInfo.S2C.LOG, _buf);
                    }

                    @Override
                    public void write(byte[] b) {
                        PacketByteBuf _buf = PacketByteBufs.create();
                        _buf.writeInt(id);
                        _buf.writeString(new String(b));
                        ServerPlayNetworking.send(player, NetInfo.S2C.LOG, _buf);
                    }

                    @Override
                    public void write(byte[] b, int offset, int len) {
                        PacketByteBuf _buf = PacketByteBufs.create();
                        _buf.writeInt(id);
                        _buf.writeString(new String(b, offset, len));
                        ServerPlayNetworking.send(player, NetInfo.S2C.LOG, _buf);
                    }
                    
                };

                try {
                    Path computer_path = JSBot.base_computer_dir.resolve("computer_" + id);
                    Path file = computer_path.resolve(file_name);
                    new JSHandler(file)
                        .setOutput(out)
                        .setError(out)
                        .addObject("js_api", new JSApi(be))
                        .setOnComplete(() -> {
                            PacketByteBuf _buf = PacketByteBufs.create();
                            _buf.writeInt(id);
                            ServerPlayNetworking.send(player, NetInfo.S2C.RUN_COMPLETED, _buf);
                        })
                        .run();
                } catch (NoSuchMethodException e) {
                    PacketByteBuf _buf = PacketByteBufs.create();
                    _buf.writeInt(id);
                    _buf.writeString("Error Running File: " + file_name + "\n");
                    ServerPlayNetworking.send(player, NetInfo.S2C.LOG, _buf);
                    PacketByteBuf d = PacketByteBufs.create();
                    d.writeInt(id);
                    ServerPlayNetworking.send(player, NetInfo.S2C.RUN_COMPLETED, d);
                    e.printStackTrace();
                }
            });
        }
    }

    public static class grabFileList implements ServerPlayNetworking.PlayChannelHandler {
        @Override
        public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            int id = buf.readInt();
            PacketByteBuf _buf = PacketByteBufs.create();
            _buf.writeInt(id);
            // _buf.writeInt(0);
            // int count = 0;
            List<String> str_list = new ArrayList<>();
            try {
                for (Path path : Files.newDirectoryStream(JSBot.base_computer_dir.resolve("computer_" + id), f -> Files.isRegularFile(f))) {
                    // _buf.writeString(path.getFileName().toString());
                    // ++count;
                    str_list.add(path.getFileName().toString());
                }
                // _buf.writerIndex(4);
                // _buf.writeInt(count);
                _buf.writeInt(str_list.size());
                for (String str : str_list) {
                    _buf.writeString(str);
                }
                ServerPlayNetworking.send(player, NetInfo.S2C.FILE_LIST, _buf);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class loadFile implements ServerPlayNetworking.PlayChannelHandler {
        @Override
        public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            int id = buf.readInt();
            String file_name = buf.readString();
            PacketByteBuf _buf = PacketByteBufs.create();

            _buf.writeInt(id);
            try {
                _buf.writeString(Files.readString(JSBot.base_computer_dir.resolve("computer_" + id + "/" + file_name)));
            } catch (IOException e) {
                _buf.writeString("");
                e.printStackTrace();
            }
            ServerPlayNetworking.send(player, NetInfo.S2C.FILE_DATA, _buf);
            
        }
    }
    
}
