package com.waterwolfies.js_bot.commands;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;

public interface ICommand {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, RegistrationEnvironment environment);
}
