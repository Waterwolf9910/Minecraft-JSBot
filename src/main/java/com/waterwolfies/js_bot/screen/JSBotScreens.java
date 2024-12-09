package com.waterwolfies.js_bot.screen;

import com.waterwolfies.js_bot.screen.handler.JSBotScreenHandlerTypes;

import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class JSBotScreens {

    // public static void register(ScreenHandlerType<?> type) {
    // HandledScreens.register(type, null);
    // }

    public static void initialize() {
        HandledScreens.register(JSBotScreenHandlerTypes.JAMES_BLOCK_SCREEN_HANDER, JamesBlockScreen::new);
    }
}
