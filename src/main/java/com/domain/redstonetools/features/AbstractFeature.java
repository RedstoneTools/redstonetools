package com.domain.redstonetools.features;

import com.domain.redstonetools.RedstoneToolsClient;
import com.domain.redstonetools.config.ConfigurationManager;
import com.domain.redstonetools.config.FeatureConfiguration;
import com.domain.redstonetools.feedback.AbstractFeedbackSender;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

import static com.domain.redstonetools.RedstoneToolsClient.INJECTOR;

public abstract class AbstractFeature {

    private final FeatureConfiguration configuration;
    private final Feature feature;

    {
        feature = getClass().getAnnotation(Feature.class);

        if (feature == null) {
            throw new IllegalStateException("Feature " + getClass() + " is not annotated with @Feature");
        }

        configuration = ConfigurationManager.get().newConfiguration(this);
    }

    public String getName() {
        return feature.name();
    }

    public String getDescription() {
        return feature.description();
    }

    public String getCommand() {
        return feature.command();
    }

    public String getIdentifier() {
        return feature.id();
    }

    /**
     * Register this feature.
     */
    public void register() {
        RedstoneToolsClient.LOGGER.info("Initializing feature(" + getIdentifier() + ")");

        CommandRegistrationCallback.EVENT.register(this::registerCommands);

        // load and save initial configuration
        // we save to make sure defaults are saved
        try {
            configuration.load();
            configuration.save();
        } catch (Exception e) {
            RedstoneToolsClient.LOGGER.error("Failed to load config for feature " + getIdentifier());
            e.printStackTrace();
        }
    }

    protected abstract void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated);
}
