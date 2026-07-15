package com.cavetthubbis72.sersfix.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static ModConfigSpec.BooleanValue enable_override;
    public static ModConfigSpec.BooleanValue block_blacklisted_dimensions;

    static {
        BUILDER.push("general_settings");
        enable_override = BUILDER.comment("If Serene Season Fix alternate season logic should be used.").define("enable_season_time_override", true);
        block_blacklisted_dimensions = BUILDER.comment("If season ticking and commands on dimensions outside the whitelist should be disabled.").define("block_blacklisted", true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
