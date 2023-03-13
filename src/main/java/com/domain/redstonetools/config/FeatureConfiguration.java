package com.domain.redstonetools.config;

import com.domain.redstonetools.features.AbstractFeature;
import com.domain.redstonetools.features.arguments.Argument;
import com.domain.redstonetools.features.arguments.TypeSerializer;
import com.domain.redstonetools.utils.ReflectionUtils;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Configuration manager for one feature.
 */
public class FeatureConfiguration {

    final ConfigurationManager manager;

    final AbstractFeature feature;
    final Path file;

    // cached option info
    final List<OptionInfo> options = new ArrayList<>();

    public FeatureConfiguration(
            ConfigurationManager manager,
            AbstractFeature feature
    ) {
        this.manager = manager;
        this.feature = feature;

        this.file = manager.getConfigurationFile(feature);

        // automatically compile
        compile();
    }

    public ConfigurationManager getManager() {
        return manager;
    }

    public AbstractFeature getFeature() {
        return feature;
    }

    public Path getFile() {
        return file;
    }

    // create a new raw configuration
    // configured for this instance
    private RawConfiguration newRaw() {
        return new RawConfiguration(manager.getFormat(), null);
    }

    /**
     * Creates the list of settings from declarations
     * in the feature class using reflection.
     */
    @SuppressWarnings("unchecked")
    public void compile() {
        try {
            ReflectionUtils.getArguments(feature.getClass())
                    .stream()
                    .forEachOrdered(arg0 -> {
                        // have to do this cast otherwise generics
                        // starts complaining which sucks
                        Argument<Object> arg = (Argument<Object>) arg0;
                        if (arg == null)
                            return;
                        if (!arg.isOptional())
                            return;

                        Accessor<Object> accessor = new Accessor<>() {
                            @Override
                            public void set(Object value) {
                                arg.withDefault(value);
                            }

                            @Override
                            public Object get() {
                                return arg.getDefaultValue();
                            }
                        };

                        OptionInfo optionInfo = new OptionInfo(
                                this,
                                "defaults",
                                arg.getName(),
                                arg,
                                (TypeSerializer<Object, Object>) arg.getType(),
                                accessor
                        );

                        options.add(optionInfo);
                    });
        } catch (Exception t) {
            throw new RuntimeException("Error occurred while compiling config for feature " + feature.getIdentifier(), t);
        }
    }

    public void save() {
        if (options.isEmpty())
            return;

        try {
            RawConfiguration config = newRaw();

            // put options
            for (OptionInfo optionInfo : options) {
                Section section = config.sectionDeep(optionInfo.section());

                Object value = optionInfo.accessor().get();
                Object saved = optionInfo.type().serialize(value);
                section.set(optionInfo.name(), saved);
            }

            config.save(file);
        } catch (Exception t) {
            throw new RuntimeException("Error occurred while saving config for feature " + feature.getIdentifier(), t);
        }
    }

    public void load() {
        if (options.isEmpty())
            return;

        try {
            if (!Files.exists(file))
                return; // no config to load

            RawConfiguration config = newRaw();
            config.load(file);

            // get options
            for (OptionInfo option : options) {
                Section sec = config.sectionDeepOrNull(option.section());
                if (sec == null)
                    continue;

                if (!sec.contains(option.name()))
                    continue;

                Object saved = sec.get(option.name());
                Object value = option.type().deserialize(saved);
                option.accessor().set(value);
            }
        } catch (Exception t) {
            throw new RuntimeException("Error occurred while loading config for feature " + feature.getIdentifier(), t);
        }
    }

}
