package com.domain.redstonetools.features.arguments;

import com.mojang.brigadier.context.CommandContext;

public class Argument<T> {
    private String name;
    private final TypeSerializer<T, ?> type;
    private boolean optional = false;
    private T value;
    private T defaultValue;

    private Argument(TypeSerializer<T, ?> type) {
        this.type = type;
    }

    public static <T> Argument<T> ofType(TypeSerializer<T, ?> type) {
        return new Argument<>(type);
    }

    public Argument<T> withDefault(T defaultValue) {
        optional = true;
        this.defaultValue = defaultValue;

        return this;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public Argument<T> named(String name) {
        this.name = name;

        return this;
    }

    public Argument<T> ensureNamed(String fieldName) {
        if (name == null) {
            name = fieldName;
        }

        return this;
    }

    public String getName() {
        return name;
    }

    public TypeSerializer<T, ?> getType() {
        return type;
    }

    public boolean isOptional() {
        return optional;
    }

    @SuppressWarnings("unchecked")
    public void setValue(CommandContext<?> context) {
        try {
            value = (T) context.getArgument(name, Object.class);
        } catch (IllegalArgumentException e) {
            if (!optional) {
                throw e;
            }

            value = defaultValue;
        }
    }

    public T getValue() {
        return value;
    }

}
