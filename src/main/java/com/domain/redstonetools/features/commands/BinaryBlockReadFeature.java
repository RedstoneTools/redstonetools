package com.domain.redstonetools.features.commands;

import com.domain.redstonetools.features.Feature;
import com.domain.redstonetools.features.arguments.Argument;
import com.domain.redstonetools.feedback.Feedback;
import com.domain.redstonetools.utils.WorldEditUtils;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sk89q.worldedit.math.BlockVector3;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;

import static com.domain.redstonetools.features.arguments.BlockStateArgumentSerializer.blockState;
import static com.domain.redstonetools.features.arguments.BoolSerializer.bool;
import static com.domain.redstonetools.features.arguments.IntegerSerializer.integer;

@Feature(id = "binary-block-read", name = "Binary Block Read", description = "Interprets your WorldEdit selection as a binary number.", command = "/read")
public class BinaryBlockReadFeature extends CommandFeature {
    private static final BlockStateArgument LIT_LAMP_ARG = new BlockStateArgument(
            Blocks.REDSTONE_LAMP.getDefaultState().with(RedstoneLampBlock.LIT, true), null, null
    );

    public static final Argument<Integer> spacing = Argument
            .ofType(integer(1))
            .withDefault(2);
    public static final Argument<BlockStateArgument> onBlock = Argument
            .ofType(blockState())
            .withDefault(LIT_LAMP_ARG);
    public static final Argument<Integer> toBase = Argument
            .ofType(integer(2, 36))
            .withDefault(10);
    public static final Argument<Boolean> reverseBits = Argument
            .ofType(bool())
            .withDefault(false);

    @Override
    protected Feedback execute(ServerCommandSource source) throws CommandSyntaxException {
        var selectionOrFeedback = WorldEditUtils.getSelection(source.getPlayer());

        if (selectionOrFeedback.right().isPresent()) {
            return selectionOrFeedback.right().get();
        }

        assert selectionOrFeedback.left().isPresent();
        var selection = selectionOrFeedback.left().get();

        var boundingBox = selection.getBoundingBox();
        var pos1 = boundingBox.getPos1();
        var pos2 = boundingBox.getPos2();
        var direction = pos2.subtract(pos1).normalize();
        var spacingVector = direction.multiply(spacing.getValue());

        if (direction.getX() + direction.getBlockY() + direction.getBlockZ() > 1) {
            return Feedback.invalidUsage("The selection must have 2 axis the same");
        }

        var onBlockState = onBlock.getValue().getBlockState();

        var bits = new StringBuilder();
        for (BlockVector3 point = pos1; boundingBox.contains(point); point = point.add(spacingVector)) {
            var pos = new BlockPos(point.getBlockX(), point.getBlockY(), point.getBlockZ());
            var blockState = source.getWorld().getBlockState(pos);

            bits.append(blockState.equals(onBlockState) ? 1 : 0);
        }

        if (reverseBits.getValue()) {
            bits.reverse();
        }

        var output = Integer.toString(Integer.parseInt(bits.toString(), 2), toBase.getValue());
        return Feedback.success(output);
    }

}