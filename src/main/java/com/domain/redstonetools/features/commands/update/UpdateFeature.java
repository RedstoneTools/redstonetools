package com.domain.redstonetools.features.commands.update;

import com.domain.redstonetools.features.Feature;
import com.domain.redstonetools.features.commands.CommandFeature;
import com.domain.redstonetools.feedback.Feedback;
import com.domain.redstonetools.utils.WorldEditUtils;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

@Feature(id = "update", name = "Update", description = "Forces block updates in the selected area.", command = "/update")
public class UpdateFeature extends CommandFeature {

    @Override
    protected Feedback execute(ServerCommandSource source) throws CommandSyntaxException {
        var selectionOrFeedback = WorldEditUtils.getSelection(source.getPlayer());
        if (selectionOrFeedback.right().isPresent()) {
            return selectionOrFeedback.right().get();
        }

        assert selectionOrFeedback.left().isPresent();
        var selection = selectionOrFeedback.left().get();

        RegionUpdater.updateRegion(source.getWorld(), source.getPlayer(), selection.getMinimumPoint(), selection.getMaximumPoint());

        return Feedback.success("Updated the selection.");
    }



}
