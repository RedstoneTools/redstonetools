package com.domain.redstonetools.features.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;

public class DoubleSerializer extends SimpleBrigadierSerializer<Double> {

    private static final DoubleSerializer INSTANCE = new DoubleSerializer(DoubleArgumentType.doubleArg());

    public static DoubleSerializer doubleArg() {
        return INSTANCE;
    }

    public static DoubleSerializer doubleArg(double min) {
        return new DoubleSerializer(DoubleArgumentType.doubleArg(min));
    }

    public static DoubleSerializer doubleArg(double min, double max) {
        return new DoubleSerializer(DoubleArgumentType.doubleArg(min, max));
    }

    private DoubleSerializer(ArgumentType<Double> argumentType) {
        super(Double.class, argumentType);
    }

}
