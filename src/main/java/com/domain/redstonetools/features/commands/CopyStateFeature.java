package com.domain.redstonetools.features.commands;

import com.domain.redstonetools.features.Feature;
import com.domain.redstonetools.feedback.Feedback;
import com.domain.redstonetools.utils.BlockInfo;
import com.domain.redstonetools.utils.BlockStateNbtUtil;
import com.mojang.datafixers.util.Either;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Method;

@Feature(id = "copy-state", name = "Copy State", description = "Gives you a copy of the block you're looking at with its BlockState.", command = "copystate")
public class CopyStateFeature extends PickBlockFeature {
    @Override
    protected Either<ItemStack, Feedback> getItemStack(ServerCommandSource source, BlockInfo blockInfo) {
        MinecraftClient client = MinecraftClient.getInstance();

        ItemStack itemStack = blockInfo.block.getPickStack(client.world, blockInfo.pos, blockInfo.state);

        if (blockInfo.state.hasBlockEntity()) {
            addBlockEntityNbt(itemStack, blockInfo.entity);
        }

        int i = addBlockStateNbt(itemStack, blockInfo.state);
        if (i == -1) {
            return Either.right(Feedback.invalidUsage("This block doesn't have any BlockState!"));
        }

        return Either.left(itemStack);
    }

    private int addBlockStateNbt(ItemStack itemStack, BlockState blockState) {
        addBlockStateText(itemStack);

        NbtCompound nbt = itemStack.getOrCreateNbt();
        String stringState = BlockStateNbtUtil.blockStateToString(blockState);
        if (stringState == null) return -1;

        nbt.putString("blockstate",stringState);

        return 1;
    }

    private void addBlockStateText(ItemStack itemStack) {
        NbtCompound displayNbt = itemStack.getSubNbt("display");
        NbtList loreList = new NbtList();

        if (displayNbt == null) {
            displayNbt = new NbtCompound();
        } else {
            loreList = (NbtList) displayNbt.get("Lore");
        }

        loreList.add(NbtString.of("\"(+BlockState)\""));
        displayNbt.put("Lore", loreList);
        itemStack.setSubNbt("display", displayNbt);
    }

    private void addBlockEntityNbt(ItemStack itemStack, BlockEntity blockEntity) {
        try {
            Class<? extends MinecraftClient> clientClass = MinecraftClient.class;
            MinecraftClient clientClassInstance = MinecraftClient.getInstance();
            Method m = clientClass.getDeclaredMethod("addBlockEntityNbt", ItemStack.class, BlockEntity.class);

            m.setAccessible(true);
            m.invoke(clientClassInstance, itemStack, blockEntity);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
