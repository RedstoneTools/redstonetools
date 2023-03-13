package com.domain.redstonetools.features.commands;

import com.domain.redstonetools.features.Feature;
import com.domain.redstonetools.features.arguments.Argument;
import com.domain.redstonetools.feedback.Feedback;
import com.domain.redstonetools.utils.BlockColor;
import com.domain.redstonetools.utils.ColoredBlock;
import com.domain.redstonetools.utils.WorldEditUtils;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.Mask2D;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockType;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

import static com.domain.redstonetools.features.arguments.BlockColorSerializer.blockColor;

@Feature(id = "color-code", name = "Color Code", description = "Color codes all color-able blocks in your WorldEdit selection.", command = "/colorcode")
public class ColorCodeFeature extends CommandFeature {
    public static final Argument<BlockColor> color = Argument
            .ofType(blockColor());
    public static final Argument<BlockColor> onlyColor = Argument
            .ofType(blockColor())
            .withDefault(null);

    private boolean shouldBeColored(World world, BlockVector3 pos, BlockColor onlyColor) {
        var state = world.getBlock(pos);
        var blockId = state.getBlockType().getId();

        var coloredBlock = ColoredBlock.fromBlockId(blockId);
        if (coloredBlock == null) return false;

        if (onlyColor == null) return true;

        var blockColor = coloredBlock.color;
        return blockColor == onlyColor;
    }

    private BaseBlock getColoredBlock(World world, BlockVector3 pos, BlockColor color) {
        var state = world.getBlock(pos);
        var blockId = state.getBlockType().getId();

        var coloredBlock = ColoredBlock.fromBlockId(blockId);
        if (coloredBlock == null) return state.toBaseBlock();

        var blockType = BlockType.REGISTRY.get(coloredBlock.withColor(color).toBlockId());
        assert blockType != null;

        return blockType.getDefaultState().toBaseBlock();
    }

    @Override
    protected Feedback execute(ServerCommandSource source) throws CommandSyntaxException {
        var player = source.getPlayer();

        var selectionOrFeedback = WorldEditUtils.getSelection(player);
        if (selectionOrFeedback.right().isPresent()) {
            return selectionOrFeedback.right().get();
        }

        assert selectionOrFeedback.left().isPresent();
        var selection = selectionOrFeedback.left().get();

        var worldEdit = WorldEdit.getInstance();
        var wePlayer = FabricAdapter.adaptPlayer(player);
        var playerSession = worldEdit.getSessionManager().get(wePlayer);

        // for each block in the selection
        final World world = FabricAdapter.adapt(player.getWorld());
        try (EditSession session = worldEdit.newEditSession(FabricAdapter.adapt(player.getWorld()))) {
            // create mask and pattern and execute block set
            int blocksColored = session.replaceBlocks(selection,
                    new Mask() {
                        @Override
                        public boolean test(BlockVector3 vector) {
                            return shouldBeColored(world, vector, onlyColor.getValue());
                        }

                        @Nullable
                        @Override
                        public Mask2D toMask2D() {
                            return null;
                        }
                    },
                    new com.sk89q.worldedit.function.pattern.Pattern() {
                        @Override
                        public BaseBlock applyBlock(BlockVector3 position) {
                            return getColoredBlock(world, position, color.getValue());
                        }
                    }
            );

            Operations.complete(session.commit());

            // call remember to allow undo
            playerSession.remember(session);

            return Feedback.success("Successfully colored " + blocksColored + " blocks " + color.getValue());
        } catch (Exception e) {
            return Feedback.error("An error occurred while coloring the blocks.");
        }
    }

}
