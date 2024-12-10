package com.waterwolfies.js_bot.commands;

import com.waterwolfies.js_bot.JSBot;
import com.waterwolfies.js_bot.js.JSHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class RunGlobal implements ICommand, Command<ServerCommandSource>, SuggestionProvider<ServerCommandSource> {
    
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, RegistrationEnvironment environment) {
        var cmd = dispatcher.register(CommandManager
            .literal("run_global_script")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager
                    .argument("script", StringArgumentType.string())
                    .suggests(this::getSuggestions)
                    .executes(this::run))
        );
        dispatcher.register(CommandManager.literal("rgs").redirect(cmd));
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Path file;
        String name = StringArgumentType.getString(context, "script");
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                context.getSource().sendMessage(Text.literal("" + b));
            }
            @Override
            public void write(byte[] b) throws IOException {
                context.getSource().sendMessage(Text.literal(new String(b)));
            }
            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                context.getSource().sendMessage(Text.literal(new String(b, off, len)));
            }
        };
        try {
            file = JSBot.global_scripts.resolve(name + ".js");
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("No such script found"));
            e.printStackTrace();
            return -1;
        }
        if (Files.exists(file)) {

        }
        try {
            new JSHandler(file).setOutput(out).setError(out).run();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        for (File file : JSBot.global_scripts.resolve("..").normalize().toFile().listFiles((File file, String name) -> name.endsWith(".js") && name.startsWith(builder.getRemaining()))) {
            String script = file.getName();
            builder.suggest(script.replace(".js", ""));
        }
        return builder.buildFuture();
    }
}
