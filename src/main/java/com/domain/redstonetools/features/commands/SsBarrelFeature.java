package com.domain.redstonetools.features.commands;

import com.domain.redstonetools.features.Feature;
import com.domain.redstonetools.features.arguments.Argument;
import com.domain.redstonetools.feedback.Feedback;
import com.domain.redstonetools.utils.RedstoneUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import static com.domain.redstonetools.features.arguments.IntegerSerializer.integer;

@Feature(id = "ss-barrel", name = "Signal Strength Barrel", description = "Creates a barrel with the specified signal strength.", command = "ss")
public class SsBarrelFeature extends CommandFeature {
    private static final int BARREL_CONTAINER_SLOTS = 27;

    public static final Argument<Integer> signalStrength = Argument
            .ofType(integer(0, 15))
            .withDefault(15);

    @Override
    protected Feedback execute(ServerCommandSource source) throws CommandSyntaxException {
        var stack = new ItemStack(Items.BARREL);

        // {BlockEntityTag:{Items:[{Slot:0,id:redstone,Count:3},{Slot:1,id:redstone,Count:61}]}}
        var items = new NbtList();

        for (int i = 0; i < RedstoneUtils.signalStrengthToNonStackableItemCount(signalStrength.getValue(), BARREL_CONTAINER_SLOTS); i++) {
            var item = new NbtCompound();
            item.putByte("Slot", (byte) i);
            item.putString("id", Registry.ITEM.getId(Items.TOTEM_OF_UNDYING).toString());
            item.putByte("Count", (byte) 1);
            items.add(item);
        }

        stack.getOrCreateSubNbt("BlockEntityTag").put("Items", items);
        stack.setCustomName(Text.of(signalStrength.getValue().toString()));
        stack.addEnchantment(Enchantment.byRawId(0),0);
        stack.getOrCreateNbt().putBoolean("HideFlags", true);

        source.getPlayer().giveItemStack(stack);

        return Feedback.none();
    }
}
