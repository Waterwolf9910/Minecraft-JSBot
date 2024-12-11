package com.waterwolfies.js_bot;

import com.waterwolfies.js_bot.blocks.JamesBlock;
import com.waterwolfies.js_bot.blocks.entity.JSBotEntityTypes;
import com.waterwolfies.js_bot.commands.RunGlobal;
import com.waterwolfies.js_bot.js.JSHandler;
import com.waterwolfies.js_bot.network.ServerPacketHandler;
import com.waterwolfies.js_bot.screen.handler.JSBotScreenHandlerTypes;

import java.nio.file.Path;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class JSBot implements ModInitializer {
    public static final String MOD_ID = "js_bot";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Path base_computer_dir;
    public static Path global_scripts;
    public static Path instance_scripts = FabricLoader.getInstance().getGameDir().resolve("local/global_js");
    public static final Block JAMES_BLOCK = new JamesBlock();
    public static final BlockItem JAMES_BLOCK_ITEM = new BlockItem(JAMES_BLOCK, new Item.Settings());
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
        String src = """
        let a = 'Hello JS'
        console.log(a)
        """;
        try {
            var engine = new ScriptEngineManager().getEngineByName("graal.js");
            if (engine != null) {
                try {
                    engine.eval(src);
                } catch (ScriptException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            new JSHandler(src).setError(System.err).setOutput(System.out).setOnComplete(() -> System.out.println("H")).run();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        CommandRegistrationCallback.EVENT.register(new RunGlobal()::register);
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "james_block"), JAMES_BLOCK);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "james_block"), JAMES_BLOCK_ITEM);
        JSBotEntityTypes.initialize();
        JSBotScreenHandlerTypes.initialize();
        ServerPacketHandler.init();

        LOGGER.info("Hello Fabric world!");
    }

}
