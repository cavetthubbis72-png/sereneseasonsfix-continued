package com.cavetthubbis72.sersfix.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cavetthubbis72.sersfix.util.SeasonUtilities;
import glitchcore.event.TickEvent;
import sereneseasons.api.SSGameRules;
import sereneseasons.season.SeasonHandler;
import sereneseasons.season.SeasonSavedData;

import java.util.HashMap;

@Mixin(value = SeasonHandler.class, remap = false)
public abstract class MixinSeasonHandler {

    @Unique
    private static final HashMap<Level, Long> sersfix$lastDayTimes = new HashMap<>();
    @Unique
    private static final HashMap<Level, Integer> sersfix$tickSinceLastUpdate = new HashMap<>();

    @Inject(method = "onWorldTick", at = @At("HEAD"), remap = false, cancellable = true)
    public void onWorldTick(TickEvent.Level event, CallbackInfo ci) {
        if (com.cavetthubbis72.sersfix.config.ServerConfig.enable_override.get()) {
            ci.cancel();

            Level world = event.getLevel();

            if (event.getPhase() == TickEvent.Phase.END && !world.isClientSide() && SeasonUtilities.isWorldWhitelisted(world)) {

                long dayTime = world.getLevelData().getDayTime();

                Long lastDayTimeObj = sersfix$lastDayTimes.get(world);
                if (lastDayTimeObj == null) {
                    lastDayTimeObj = dayTime;
                }
                long lastDayTime = lastDayTimeObj;
                sersfix$lastDayTimes.put(world, dayTime);

                if (!SeasonUtilities.getProgressSeasonWhileOffline()) {
                    MinecraftServer server = world.getServer();
                    if (server != null && server.getPlayerList().getPlayerCount() == 0)
                        return;
                }

                if (!world.getGameRules().getBoolean(SSGameRules.RULE_DOSEASONCYCLE))
                    return;

                long difference = dayTime - lastDayTime;

                if (difference == 0) {
                    return;
                }

                SeasonSavedData savedData = SeasonHandler.getSeasonSavedData(world);
                SeasonUtilities.setSeasonCycleTicks(savedData, savedData.seasonCycleTicks + difference);

                Integer tickObj = sersfix$tickSinceLastUpdate.get(world);
                int tick = tickObj != null ? tickObj : 0;
                if (tick >= 20) {
                    SeasonHandler.sendSeasonUpdate(world);
                    tick %= 20;
                }
                sersfix$tickSinceLastUpdate.put(world, tick + 1);
            }
        }
    }
}
