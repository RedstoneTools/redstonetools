package com.domain.redstonetools.features.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

public class IntegerSerializer extends SimpleBrigadierSerializer<Integer> {

    private static final IntegerSerializer INSTANCE = new IntegerSerializer(IntegerArgumentType.integer());

    public static IntegerSerializer integer() {
        return INSTANCE;
    }

    public static IntegerSerializer integer(int min) {
        return new IntegerSerializer(IntegerArgumentType.integer(min));
    }

    public static IntegerSerializer integer(int min, int max) {
        return new IntegerSerializer(IntegerArgumentType.integer(min, max));
    }

    private IntegerSerializer(ArgumentType<Integer> argumentType) {
        super(Integer.class, argumentType);
    }

}
