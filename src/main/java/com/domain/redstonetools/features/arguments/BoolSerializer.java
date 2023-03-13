package com.domain.redstonetools.features.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;

public class BoolSerializer extends SimpleBrigadierSerializer<Boolean> {

    private static final BoolSerializer INSTANCE = new BoolSerializer(BoolArgumentType.bool());

    public static BoolSerializer bool() {
        return INSTANCE;
    }

    private BoolSerializer(ArgumentType<Boolean> argumentType) {
        super(Boolean.class, argumentType);
    }

}
