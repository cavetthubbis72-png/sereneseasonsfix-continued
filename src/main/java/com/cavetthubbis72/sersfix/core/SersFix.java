package com.cavetthubbis72.sersfix.core;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.cavetthubbis72.sersfix.command.SeasonTimeCommands;
import com.cavetthubbis72.sersfix.config.ServerConfig;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.fml.loading.FMLPaths;

@Mod(SersFix.MODID)
public class SersFix {
    public static final String MODID = "sersfix";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public SersFix(IEventBus modEventBus, ModContainer container) {
        LOGGER.info("Serene Seasons Fix NeoForge 1.21.1 loaded");

        Path configPath = FMLPaths.CONFIGDIR.get();
        Path modConfigPath = Paths.get(configPath.toAbsolutePath().toString(), "sersfix");
        try {
            Files.createDirectory(modConfigPath);
        } catch (FileAlreadyExistsException e) {
            LOGGER.debug("Using existing sersfix config directory");
        } catch (IOException e) {
            LOGGER.error("Failed to create sersfix config directory", e);
        }

        container.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        SeasonTimeCommands.register(event.getDispatcher());
    }
}
