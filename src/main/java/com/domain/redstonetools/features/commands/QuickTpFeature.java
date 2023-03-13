package com.domain.redstonetools.features.commands;

import com.domain.redstonetools.features.Feature;
import com.domain.redstonetools.features.arguments.Argument;
import com.domain.redstonetools.feedback.Feedback;
import com.domain.redstonetools.utils.PositionUtils;
import com.domain.redstonetools.utils.RaycastUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import static com.domain.redstonetools.RedstoneToolsClient.LOGGER;
import static com.domain.redstonetools.features.arguments.BoolSerializer.bool;
import static com.domain.redstonetools.features.arguments.FloatSerializer.floatArg;

@Feature(id = "quick-tp", name = "Quick TP", description = "Teleports you in the direction you are looking.", command = "quicktp")
public class QuickTpFeature extends CommandFeature {

    public static final Argument<Float> distance = Argument
            .ofType(floatArg(1.0f))
            .withDefault(50.0f);
    public static final Argument<Boolean> includeFluids = Argument
            .ofType(bool())
            .withDefault(false);

    @Override
    protected Feedback execute(ServerCommandSource source) throws CommandSyntaxException {
        var player = source.getPlayer();

        var targetPosition = getTargetPosition(player);

        player.teleport(targetPosition.x, targetPosition.y, targetPosition.z);

        return Feedback.none();
    }

    private Vec3d getTargetPosition(PlayerEntity player) {
        // 8 chunks default, 16 blocks per chunk
        var renderDistanceBlocks = PlayerEntity.getRenderDistanceMultiplier() * 8 * 16;
        LOGGER.error("Raycasting distance: {}", Math.min(distance.getValue(), renderDistanceBlocks));
        var hit = player.raycast(Math.min(distance.getValue(), renderDistanceBlocks), 0, includeFluids.getValue());

        return clampHitPosition(hit).subtract(0, 1.12, 0);
    }

    private Vec3d clampHitPosition(HitResult hit) {
        if (hit.getType() != HitResult.Type.BLOCK) {
            return hit.getPos().subtract(0, 0.5, 0);
        }

        var blockHit = (BlockHitResult) hit;

        var neighbor = RaycastUtils.getBlockHitNeighbor(blockHit);
        var neighborPos = neighbor.getBlockPos();

        return PositionUtils.getBottomPositionOfBlock(neighborPos);
    }
}
