package com.domain.redstonetools.config;

import com.domain.redstonetools.RedstoneToolsClient;
import com.domain.redstonetools.features.AbstractFeature;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.representer.Representer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * The global configuration manager.
 */
public class ConfigurationManager {

    static final ConfigurationManager INSTANCE =
            new ConfigurationManager();

    public static ConfigurationManager get() {
        return INSTANCE;
    }

    /*
        SnakeYAML Instance
     */

    private static final DumperOptions DUMPER_OPTIONS = new DumperOptions();
    private static final LoaderOptions LOADER_OPTIONS = new LoaderOptions();

    static {
        DUMPER_OPTIONS.setProcessComments(true);
        DUMPER_OPTIONS.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        DUMPER_OPTIONS.setPrettyFlow(true);
        DUMPER_OPTIONS.setAllowUnicode(true);

        LOADER_OPTIONS.setProcessComments(true);
    }

    private static final Yaml YAML = new Yaml(new Constructor(LOADER_OPTIONS), new Representer(DUMPER_OPTIONS), DUMPER_OPTIONS, LOADER_OPTIONS);

    /** Configuration Format */
    private static final RawFormat FORMAT = RawFormat.ofYaml(YAML);

    /////////////////////////////////////////

    final Path directory = Path.of("./config/redstonetools/");
    final Map<String, FeatureConfiguration> configs = new HashMap<>();

    {
        try {
            if (!Files.exists(directory))
                Files.createDirectories(directory);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Failed to create config directory " + directory);
        }
    }

    public Path getConfigurationFile(AbstractFeature feature) {
        return directory.resolve(feature.getIdentifier() + ".yml");
    }

    public FeatureConfiguration getConfiguration(String id) {
        return configs.get(id);
    }

    public FeatureConfiguration newConfiguration(AbstractFeature feature) {
        FeatureConfiguration config = new FeatureConfiguration(this, feature);
        configs.put(feature.getIdentifier(), config);
        return config;
    }

    public void reloadAll() {
        RedstoneToolsClient.LOGGER.info("Reloading all feature configs");
        for (FeatureConfiguration config : configs.values()) {
            config.load();
        }
    }

    public RawFormat getFormat() {
        return FORMAT;
    }

}
