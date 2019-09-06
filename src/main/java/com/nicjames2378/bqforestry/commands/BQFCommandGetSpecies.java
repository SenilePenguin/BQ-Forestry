package com.nicjames2378.bqforestry.commands;

import com.google.common.collect.Lists;
import com.nicjames2378.bqforestry.utils.UtilitiesBee;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;

public class BQFCommandGetSpecies implements ICommand {
    private final TextComponentTranslation improperItemError = format(TextFormatting.RED, "Please hold a properly formatted bee before using this command!");
    private final TextComponentTranslation clientOnlyError = format(TextFormatting.RED, "This command can only be issued by a player in-game.");

    @Override
    public String getName() {
        return "getspecies";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/getspecies";
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = Lists.<String>newArrayList();
        aliases.add("/getspecies");
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
            // Gets player and held item
            EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
            ItemStack heldItem = player.getHeldItemMainhand();
            String species = UtilitiesBee.getBeeSpecies(heldItem);

            if (heldItem.isEmpty() || species == null) {
                sender.sendMessage(improperItemError);
            } else {
                player.sendMessage(new TextComponentTranslation("bqforestry.command.getspecies", TextFormatting.YELLOW.toString() + species));
                copyToClipboard(species);
            }
        } else {
            sender.sendMessage(clientOnlyError);
        }
    }

    private static void copyToClipboard(String message) {
        StringSelection stringSelection = new StringSelection(message);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    private TextComponentTranslation format(TextFormatting color, String str, Object... args) {
        TextComponentTranslation ret = new TextComponentTranslation(str, args);
        ret.getStyle().setColor(color);
        return ret;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand iCommand) {
        return 0;
    }
}
