package com.bion.roleplayai;

import com.bion.roleplayai.command.RoleplAICommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleplAI implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("roleplayai");
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(RoleplAICommand::register);
    }
}
