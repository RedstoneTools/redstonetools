package com.domain.redstonetools.features.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;

public class LongSerializer extends SimpleBrigadierSerializer<Long> {

    private static final LongSerializer INSTANCE = new LongSerializer(LongArgumentType.longArg());

    public static LongSerializer longArg() {
        return INSTANCE;
    }

    public static LongSerializer longArg(long min) {
        return new LongSerializer(LongArgumentType.longArg(min));
    }

    public static LongSerializer longArg(long min, long max) {
        return new LongSerializer(LongArgumentType.longArg(min, max));
    }

    private LongSerializer(ArgumentType<Long> argumentType) {
        super(Long.class, argumentType);
    }

}
