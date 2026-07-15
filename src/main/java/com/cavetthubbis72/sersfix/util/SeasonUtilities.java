package com.cavetthubbis72.sersfix.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.season.SeasonTime;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SeasonUtilities {

    private static Object seasonsConfigInstance;
    private static Field progressSeasonWhileOfflineField;
    private static Method isDimensionWhitelistedMethod;

    static {
        try {
            Class<?> seasonsConfigClass = Class.forName("sereneseasons.config.SeasonsConfig");
            Field instanceField = seasonsConfigClass.getDeclaredField("INSTANCE");
            instanceField.setAccessible(true);
            seasonsConfigInstance = instanceField.get(null);

            try {
                progressSeasonWhileOfflineField = seasonsConfigClass.getDeclaredField("progressSeasonWhileOffline");
                progressSeasonWhileOfflineField.setAccessible(true);
            } catch (NoSuchFieldException ignored) {
            }

            try {
                isDimensionWhitelistedMethod = seasonsConfigClass.getDeclaredMethod("isDimensionWhitelisted", ResourceKey.class);
                isDimensionWhitelistedMethod.setAccessible(true);
            } catch (NoSuchMethodException ignored) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long calculateCycleTicks(long seasonCycleTicks) {
        int cycleDuration = SeasonTime.ZERO.getCycleDuration();
        return (seasonCycleTicks % cycleDuration + cycleDuration) % cycleDuration;
    }

    public static void setSeasonCycleTicks(SeasonSavedData seasonSavedData, long seasonCycleTicks) {
        seasonSavedData.seasonCycleTicks = (int) calculateCycleTicks(seasonCycleTicks);
        seasonSavedData.setDirty();
    }

    public static boolean isWorldWhitelisted(Level world) {
        if (!com.cavetthubbis72.sersfix.config.ServerConfig.block_blacklisted_dimensions.get()) {
            return true;
        }
        try {
            if (isDimensionWhitelistedMethod != null && seasonsConfigInstance != null) {
                return (boolean) isDimensionWhitelistedMethod.invoke(seasonsConfigInstance, world.dimension());
            }
        } catch (Exception ignored) {
        }
        return true;
    }

    public static boolean getProgressSeasonWhileOffline() {
        try {
            if (progressSeasonWhileOfflineField != null && seasonsConfigInstance != null) {
                Object value = progressSeasonWhileOfflineField.get(seasonsConfigInstance);
                if (value instanceof Boolean) return (boolean) value;
                if (value != null) {
                    Method getMethod = value.getClass().getMethod("get");
                    return (boolean) getMethod.invoke(value);
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }
}
