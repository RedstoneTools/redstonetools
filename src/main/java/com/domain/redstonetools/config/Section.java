package com.domain.redstonetools.config;

import net.minecraft.client.render.debug.DebugRenderer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A section of a configuration.
 */
public interface Section {

    @SuppressWarnings("unchecked")
    static Section memory(final Map<String, Object> map) {
        return new Section() {
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
                    Map<String, Object> map = new HashMap<>();
                    map.put(key, map);
                    return Section.memory(map);
                } else if (v instanceof Map map) {
                    return Section.memory(map);
                } else if (v instanceof Section section) {
                    return section;
                }

                throw new IllegalStateException("Value by key '" + key + "' is not a section or map");
            }
        };
    }

    /////////////////////////////////////

    /**
     * Get the keys of this section.
     *
     * @return The keys.
     */
    Set<String> getKeys();

    /**
     * Get the values of this section.
     *
     * @return The values.
     */
    Collection<Object> getValues();

    /**
     * Get one value by key.
     *
     * @param key The key.
     * @param <T> The value type.
     * @return The value.
     */
    <T> T get(String key);

    /**
     * Get one value by key or the
     * default value if absent.
     *
     * @param key The key.
     * @param def The default value.
     * @param <T> The value type.
     * @return The value.
     */
    <T> T getOrDefault(String key, T def);

    /**
     * Get one value by key or the
     * default value from the supplier if absent.
     *
     * @param key The key.
     * @param def The default value supplier.
     * @param <T> The value type.
     * @return The value.
     */
    <T> T getOrSupply(String key, Supplier<T> def);

    /**
     * Set one value by key.
     *
     * @param key The key.
     * @param value The value.
     */
    void set(String key, Object value);

    /**
     * Check if this section contains
     * the provided key.
     *
     * @param key The key.
     * @return If it contains.
     */
    boolean contains(String key);

    /**
     * Get or create a section by key.
     *
     * @param key The key.
     * @return The section.
     */
    Section section(String key);

    default Section sectionDeep(String key) {
        if (key == null || key.isBlank())
            return this;

        String[] split = key.split("\\.");
        Section curr = this;
        for (String e : split) {
            curr = curr.section(e);
        }

        return curr;
    }

    default Section sectionDeepOrNull(String key) {
        if (key == null || key.isBlank())
            return this;

        String[] split = key.split("\\.");
        Section curr = this;
        for (String e : split) {
            curr = curr.section(e);
            if (curr == null)
                return null;
        }

        return curr;
    }

}
