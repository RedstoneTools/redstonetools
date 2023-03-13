package com.domain.redstonetools.features.commands;

import com.domain.redstonetools.features.Feature;
import com.domain.redstonetools.features.arguments.Argument;
import com.domain.redstonetools.feedback.Feedback;
import net.minecraft.server.command.ServerCommandSource;

import static com.domain.redstonetools.features.arguments.IntegerSerializer.integer;
import static com.domain.redstonetools.features.arguments.StringSerializer.word;

import java.math.BigInteger;

@Feature(id = "base-convert", name = "Base Convert", description = "Converts a number from one base to another.", command = "base")
public class BaseConvertFeature extends CommandFeature {
    public static final Argument<Integer> fromBase = Argument
            .ofType(integer(2, 36));
    public static final Argument<String> number = Argument
            .ofType(word());
    public static final Argument<Integer> toBase = Argument
            .ofType(integer(2, 36));

    @Override
    protected Feedback execute(ServerCommandSource source) {
        BigInteger input;
        try {
            input = new BigInteger(number.getValue(), fromBase.getValue());
        } catch (NumberFormatException e) {
            return Feedback.invalidUsage("Inputted number does not match the specified base");
        }

        var output = input.toString(toBase.getValue());
        return Feedback.success(output);
    }

}
