package com.cavetthubbis72.sersfix.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import com.cavetthubbis72.sersfix.util.SeasonUtilities;
import sereneseasons.season.SeasonHandler;
import sereneseasons.season.SeasonSavedData;

public class SeasonTimeCommands {

    public static void register(com.mojang.brigadier.CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("season")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("time")
                                .requires(cs -> cs.hasPermission(2))
                                .then(Commands.literal("info").executes((ctx) -> {
                                    Level world = ctx.getSource().getLevel();
                                    return infoSeasonTime(ctx.getSource(), world);
                                }))
                                .then(Commands.literal("sync").executes((ctx) -> {
                                    Level world = ctx.getSource().getLevel();
                                    return syncSeasonTime(ctx.getSource(), world);
                                }))
                                .then(Commands.literal("set").then(Commands.argument("time", TimeArgument.time()).executes((ctx) -> {
                                    Level world = ctx.getSource().getLevel();
                                    return setSeasonTime(ctx.getSource(), world, IntegerArgumentType.getInteger(ctx, "time"));
                                })))
                                .then(Commands.literal("add").then(Commands.argument("time", TimeArgument.time()).executes((ctx) -> {
                                    Level world = ctx.getSource().getLevel();
                                    return addSeasonTime(ctx.getSource(), world, IntegerArgumentType.getInteger(ctx, "time"));
                                })))
                        )
        );
    }

    private static int infoSeasonTime(CommandSourceStack cs, Level world) {
        SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(world);
        int seasonTime = seasonData.seasonCycleTicks;
        long dayTime = world.getLevelData().getDayTime();
        long delta = seasonTime - SeasonUtilities.calculateCycleTicks(dayTime);
        cs.sendSuccess(() -> {
            boolean whitelisted = SeasonUtilities.isWorldWhitelisted(world);
            return Component.translatable("commands.sersfix.time.info", seasonTime, dayTime, delta, whitelisted);
        }, true);
        return (int) delta;
    }

    private static int syncSeasonTime(CommandSourceStack cs, Level world) {
        if (SeasonUtilities.isWorldWhitelisted(world)) {
            SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(world);
            SeasonUtilities.setSeasonCycleTicks(seasonData, world.getLevelData().getDayTime());
            SeasonHandler.sendSeasonUpdate(world);
            cs.sendSuccess(() -> Component.translatable("commands.sersfix.time.sync_season.success"), true);
            return seasonData.seasonCycleTicks;
        } else {
            cs.sendSuccess(() -> Component.translatable("commands.sersfix.time.sync_season.not_whitelisted"), true);
            return -1;
        }
    }

    private static int setSeasonTime(CommandSourceStack cs, Level world, int time) {
        if (SeasonUtilities.isWorldWhitelisted(world)) {
            SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(world);
            SeasonUtilities.setSeasonCycleTicks(seasonData, time);
            SeasonHandler.sendSeasonUpdate(world);
            cs.sendSuccess(() -> Component.translatable("commands.sersfix.time.set_season.success", seasonData.seasonCycleTicks), true);
            return seasonData.seasonCycleTicks;
        } else {
            cs.sendSuccess(() -> Component.translatable("commands.sersfix.time.set_season.not_whitelisted"), true);
            return -1;
        }
    }

    private static int addSeasonTime(CommandSourceStack cs, Level world, int time) {
        if (SeasonUtilities.isWorldWhitelisted(world)) {
            SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(world);
            SeasonUtilities.setSeasonCycleTicks(seasonData, seasonData.seasonCycleTicks + time);
            SeasonHandler.sendSeasonUpdate(world);
            cs.sendSuccess(() -> Component.translatable("commands.sersfix.time.set_season.success", seasonData.seasonCycleTicks), true);
            return seasonData.seasonCycleTicks;
        } else {
            cs.sendSuccess(() -> Component.translatable("commands.sersfix.time.set_season.not_whitelisted"), true);
            return -1;
        }
    }
}
