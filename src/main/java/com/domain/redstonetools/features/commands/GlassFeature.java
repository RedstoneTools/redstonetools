package com.domain.redstonetools.features.commands;

import com.domain.redstonetools.features.Feature;
import com.domain.redstonetools.feedback.Feedback;
import com.domain.redstonetools.utils.BlockColor;
import com.domain.redstonetools.utils.BlockInfo;
import com.domain.redstonetools.utils.ColoredBlock;
import com.mojang.datafixers.util.Either;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;

@Feature(id = "glass", name = "Glass", description = "Converts colored blocks to their glass variant and glass to wool.", command = "glass")
public class GlassFeature extends PickBlockFeature {
    @Override
    protected boolean requiresBlock() {
        return false;
    }

    @Override
    protected Either<ItemStack, Feedback> getItemStack(ServerCommandSource source, @Nullable BlockInfo blockInfo) {
        if (blockInfo == null) {
            return Either.left(new ItemStack(Blocks.GLASS));
        }

        var coloredBlock = getColoredGlassOrWoolVariant(blockInfo.block);

        return Either.left(coloredBlock == null
            ? new ItemStack(Blocks.GLASS)
            : new ItemStack(coloredBlock.toBlock()));
    }

    private ColoredBlock getColoredGlassOrWoolVariant(Block block) {
        var blockId = Registry.BLOCK.getId(block).toString();

        var coloredBlock = ColoredBlock.fromBlockId(blockId);
        if (coloredBlock == null) return null;

        return (isGlass(coloredBlock)
            ? getColoredWool()
            : getColoredGlass()
        ).withColor(coloredBlock.color);
    }

    private boolean isGlass(ColoredBlock coloredBlock) {
        var whiteVariant = coloredBlock.withColor(BlockColor.WHITE).toBlockId();

        var whiteVariantId = Identifier.tryParse(whiteVariant);

        return Registry.BLOCK.get(whiteVariantId).equals(Blocks.WHITE_STAINED_GLASS);
    }

    private ColoredBlock getColoredWool() {
        return ColoredBlock.fromBlock(Blocks.WHITE_WOOL);
    }

    private ColoredBlock getColoredGlass() {
        return ColoredBlock.fromBlock(Blocks.WHITE_STAINED_GLASS);
    }
}
