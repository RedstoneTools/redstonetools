package com.domain.redstonetools.features.arguments;

import com.mojang.brigadier.arguments.ArgumentType;

public abstract class SimpleBrigadierSerializer<T> extends BrigadierSerializer<T, T> {

    public SimpleBrigadierSerializer(Class<T> clazz, ArgumentType<T> argumentType) {
        super(clazz, argumentType);
    }

    @Override
    public T deserialize(T serialized) {
        return serialized;
    }

    @Override
    public T serialize(T value) {
        return value;
    }

}
