package com.domain.redstonetools.config;

import org.apache.commons.io.input.NullReader;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * File-based configuration
 * wrapped for ease of use.
 * @author orbyfied
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class RawConfiguration implements Section {

    public static Writer createFileWriter(Path file) {
        try {
            if (!Files.exists(file)) {
                try {
                    if (!Files.exists(file.getParent()))
                        Files.createDirectories(file.getParent());
                    Files.createFile(file);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }

            return Files.newBufferedWriter(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create writer for file " + file, e);
        }
    }

    public static Reader createFileReader(Path file) {
        try {
            if (!Files.exists(file))
                return new NullReader();
            return Files.newBufferedReader(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create reader for file " + file, e);
        }
    }

    ///////////////////////////////////////////////////////

    /**
     * The resource resolver.
     */
    final Function<String, InputStream> resourceResolver;

    /**
     * The internal configuration.
     */
    Map<String, Object> map;

    /**
     * The configuration format.
     */
    final RawFormat format;

    /**
     * Constructor.
     */
    public RawConfiguration(
            RawFormat format,
            Function<String, InputStream> resourceResolver
    ) {
        this.resourceResolver = resourceResolver;
        this.format = format;

        // create empty map
        this.map = new LinkedHashMap<>();
    }

    /* Basic Getters. */

    public Map<String, Object> map() { return map; }

    public RawConfiguration load(Reader reader) {
        Objects.requireNonNull(reader, "Reader can not be null");

        try {
            // load configuration
            this.map = format.load(reader);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration", e);
        }

        return this;
    }

    public RawConfiguration load(InputStream inputStream) {
        Objects.requireNonNull(inputStream, "Input stream can not be null");
        return load(new InputStreamReader(inputStream));
    }

    public RawConfiguration load(Path file) {
        return load(createFileReader(file));
    }

    public RawConfiguration save(Writer writer) {
        Objects.requireNonNull(writer, "Writer can not be null");

        try {
            format.save(writer, map);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save configuration", e);
        }

        return this;
    }

    public RawConfiguration save(Path file) {
        return save(createFileWriter(file));
    }

    //////////////////////////////////////
    ////////// WRAPPED METHODS ///////////
    //////////////////////////////////////

    @Override
    public Set<String> getKeys() {
        return map.keySet();
    }

    @Override
    public Collection<Object> getValues() {
        return map.values();
    }

    @Override
    public <T> T get(String key) {
        return (T) map.get(key);
    }

    @Override
    public <T> T getOrDefault(String key, T def) {
        return (T) map.getOrDefault(key, def);
    }

    @Override
    public <T> T getOrSupply(String key, Supplier<T> def) {
        if (!map.containsKey(key))
            return def.get();
        return (T) map.get(key);
    }

    @Override
    public void set(String key, Object value) {
        map.put(key, value);
    }

    @Override
    public boolean contains(String key) {
        return map.containsKey(key);
    }

    @Override
    public Section section(String key) {
        Object v = map.get(key);
        if (v == null) {
            Map<String, Object> map = new LinkedHashMap<>();
            this.map.put(key, map);
            return Section.memory(map);
        } else if (v instanceof Map map) {
            return Section.memory(map);
        } else if (v instanceof Section section) {
            return section;
        }

        throw new IllegalStateException("Value by key '" + key + "' is not a section or map");
    }

}