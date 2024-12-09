package com.waterwolfies.js_bot.screen.handler;

import com.waterwolfies.js_bot.JSBot;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class JSBotScreenHandlerTypes {

    public static <T extends ScreenHandler> ScreenHandlerType<T> registerSimple(String path,
            ScreenHandlerType.Factory<T> screen_handler_factory) {
        return Registry.register(Registries.SCREEN_HANDLER, Identifier.of(JSBot.MOD_ID, path),
                new ScreenHandlerType<>(screen_handler_factory, FeatureSet.empty()));
    }

    public static <T extends ScreenHandler> ExtendedScreenHandlerType<T> registerExtended(String path,
            ExtendedScreenHandlerType.ExtendedFactory<T> screen_handler_factory) {
        return Registry.register(Registries.SCREEN_HANDLER, Identifier.of(JSBot.MOD_ID, path),
                new ExtendedScreenHandlerType<>(screen_handler_factory));
    }

    public static ScreenHandlerType<JamesBlockScreenHandler> JAMES_BLOCK_SCREEN_HANDER = registerExtended(
            "james_block_screen", JamesBlockScreenHandler::new);

    public static void initialize() {

    }
}
