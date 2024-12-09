package com.waterwolfies.js_bot;

import com.waterwolfies.js_bot.network.ClientPacketHandler;
import com.waterwolfies.js_bot.screen.JSBotScreens;

import net.fabricmc.api.ClientModInitializer;

public class JSBotClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        JSBotScreens.initialize();
        ClientPacketHandler.init();
    }
    
}
