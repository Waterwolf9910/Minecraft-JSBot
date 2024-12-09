package com.waterwolfies.js_bot.network;

import com.waterwolfies.js_bot.screen.JamesBlockScreen;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class ClientPacketHandler {
    
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(NetInfo.S2C.LOG, new log()::receive);
        ClientPlayNetworking.registerGlobalReceiver(NetInfo.S2C.FILE_LIST, new fileList()::receive);
        ClientPlayNetworking.registerGlobalReceiver(NetInfo.S2C.RUN_COMPLETED, new runComplete()::receive);
        ClientPlayNetworking.registerGlobalReceiver(NetInfo.S2C.FILE_DATA, new fileData()::receive);
    }

    public static class log implements ClientPlayNetworking.PlayChannelHandler {
        @Override
        public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            int id = buf.readInt();
            String str = buf.readString();
            client.execute(() -> {
                if (client.currentScreen instanceof JamesBlockScreen screen) {
                    if (screen.getID() == id) {
                        screen.addLog(str);
                    }
                }
            });
        }
    }

    public static class fileList implements ClientPlayNetworking.PlayChannelHandler {
        @Override
        public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            
            int id = buf.readInt();
            int count = buf.readInt();
            List<String> file_list = new ArrayList<>();
            while (count > 0) {
                --count;
                file_list.add(buf.readString());
            }
            client.execute(() -> {
                if (client.currentScreen instanceof JamesBlockScreen screen) {
                    if (screen.getID() == id) {
                        screen.file_list = file_list;
                    }
                }
            });
        }
    }

    public static class fileData implements ClientPlayNetworking.PlayChannelHandler {
        @Override
        public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            int id = buf.readInt();
            String file_data = buf.readString();

            client.execute(() -> {
                if (client.currentScreen instanceof JamesBlockScreen screen) {
                    if (screen.getID() == id) {
                        screen.setFileData(file_data);
                    }
                }
            });
            
        }
    }

    public static class runComplete implements ClientPlayNetworking.PlayChannelHandler {
        @Override
        public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            int id = buf.readInt();

            client.execute(() -> {
                if (client.currentScreen instanceof JamesBlockScreen screen) {
                    if (id == screen.getID()) {
                        screen.completed();
                    }
                }
            });
            
        }
    }
}
