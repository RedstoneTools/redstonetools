package com.domain.redstonetools.features.commands;

import com.domain.redstonetools.features.Feature;
import com.domain.redstonetools.feedback.Feedback;
import com.domain.redstonetools.utils.CommandSourceUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

@Feature(id = "redstoner", name = "Redstoner", description = "Sets the gamerules to be more redstone friendly.", command = "redstoner")
public class RedstonerFeature extends CommandFeature {
    @Override
    protected Feedback execute(ServerCommandSource source) throws CommandSyntaxException {
        List.of(
                "gamerule doTileDrops false",
                "gamerule doTraderSpawning false",
                "gamerule doWeatherCycle false",
                "gamerule doDaylightCycle false",
                "gamerule doMobSpawning false",
                "gamerule doContainerDrops false",
                "time set noon"
                ).forEach(cmd -> CommandSourceUtils.executeCommand(source, cmd));

        return Feedback.none();
    }
}
