package com.domain.redstonetools.config;

/**
 * Provides methods to get and set values
 * for a setting.
 */
public interface Accessor<T> {

    void set(T value);

    T get();

}
