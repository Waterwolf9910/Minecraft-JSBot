package com.waterwolfies.js_bot;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

import com.waterwolfies.js_bot.commands.RunGlobal;
import com.waterwolfies.js_bot.js.JSHandler;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSBot implements ModInitializer {
    public static final String MOD_ID = "js-bot";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Path global_scripts;
    public static Path instance_scripts = FabricLoader.getInstance().getGameDir().resolve("local/global_js");
    // public static final ScriptEngine graaljs = new ScriptEngineManager().getEngineByName("Graal.js");

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
        instance_scripts.toFile().mkdirs();
        // String src = """
        //     let a = 'Hello JS'
        //     console.log(a, test)
        //     try {
        //         test.setB("E")
        //         console.log(map)
        //     } catch (err) {
        //         console.log("error", err)
        //     }
        //     map.put("test", "2")
        //     // Not functioning
        //     // try {
        //     //     let clazz = Java.type("com.waterwolfies.js_bot.test")
        //     //     console.log(clazz))
        //     //     console.log(new clazz())
        //     // } catch (err) {
        //     //     console.log(err)
        //     // }
        //     console.log("Ran")
        //     a
        // """;
        // var t = new test();
        // Map<String, String> map = new HashMap<>();
        // map.put("script", src);
        
        // try {
        //     new JSHandler(src)
        //         .addObject("test", t)
        //         .addObject("map", map)
        //         .run();
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        // LOGGER.info(t.getB());
        CommandRegistrationCallback.EVENT.register(new RunGlobal()::register);
        LOGGER.info("Hello Fabric world!");
    }

}
