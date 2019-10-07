package com.nicjames2378.bqforestry.commands;

import betterquesting.api2.utils.QuestTranslation;
import com.google.common.collect.Lists;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import com.nicjames2378.bqforestry.utils.UtilitiesBee;
import forestry.api.apiculture.EnumBeeChromosome;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
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
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class BQFCommandFindTrait implements ICommand {
    private String commandKey = "/findtrait";

    private enum ValidChromosomes {
        INVALID,
        Bee
//        Tree
    }

    private static ValidChromosomes getEnumValid(ICommandSender sender) {
        if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
            // Gets player and held item
            EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
            ItemStack heldItem = player.getHeldItemMainhand();

            if (UtilitiesBee.hasValidGrowthLevel(heldItem)) return ValidChromosomes.Bee;
        }
        return ValidChromosomes.INVALID;
    }

    private static ArrayList<String> getChromosomes(ValidChromosomes type) {
        ArrayList<String> values = new ArrayList<String>();

        switch (type) {
            case Bee:
                for (EnumBeeChromosome e : EnumBeeChromosome.values())
                    values.add(e.getName().toLowerCase());
                break;
//            case Tree:
//                values.add("NotYetImplemented");
//                break;
            default:
                break;
        }
        return values;
    }

    @Override
    public String getName() {
        // Just removed the "/" from the beginning
        return commandKey.substring(1);
    }

    @Override
    public String getUsage(ICommandSender sender) {

        ValidChromosomes en = getEnumValid(sender);
        ArrayList<String> list = getChromosomes(en);
        StringBuilder sb = new StringBuilder();

        if (list.size() < 1) {
            return QuestTranslation.translate("bqforestry.command.gettrait.usage", commandKey);
        }

        sb.append(commandKey).append(" <");
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append("|");
        }
        sb.append(">");

        return sb.toString();

        //return QuestTranslation.translate("bqforestry.command.gettrait.usage", commandKey);
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = Lists.<String>newArrayList();
        aliases.add(commandKey);
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        // Make sure command is run in-game from player
        if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
            // Make sure argument is even there before checking everything else
            if (args.length < 1 || args[0] == null)
                throw new WrongUsageException("bqforestry.command.error.improperargument");

            // Gets player and held item
            EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();

            ValidChromosomes en = getEnumValid(sender);
            ArrayList<String> list = getChromosomes(en);
            String trait = "";

            String[] aa;
            // Ensure the player is holding a valid item (or give an error message)
            switch (en) {
                case Bee:
                    if (list.contains(args[0].toLowerCase())) {
                        // It's safe to assume a [0] here since bees using my NBT handling shouldn't actually be obtainable in-game
                        trait = UtilitiesBee.getTrait(player.getHeldItemMainhand(), EnumBeeChromosome.valueOf(args[0].toUpperCase()), true)[0];
                        break;
                    }
                    //case Tree:
                case INVALID:
                    throw new WrongUsageException("bqforestry.command.error.improperitem", "item");
            }

            if (!trait.equals("")) {
                // Handles whether to copy to clipboard or not
                if (args.length < 2 && ConfigHandler.cfgDefaultCommandCopy) { // Doesn't have a second argument, so get inference from Config
                    // Has second argument that *is* true (do a true check instead of a false check in case of typos)
                    displayTraitWithCopy(player, trait, true);

                } else if (args.length >= 2 && args[1].toLowerCase().equals("true")) {
                    displayTraitWithCopy(player, trait, true);

                } else {
                    displayTraitWithCopy(player, trait, false);
                }
            } else throw new WrongUsageException("bqforestry.command.error.improperargument");
        } else { // Not a player
            throw new WrongUsageException("bqforestry.command.error.clientonly");
        }
    }

    private void displayTraitWithCopy(EntityPlayer player, String trait, boolean copy) {
        TextComponentTranslation getTrait = new TextComponentTranslation("bqforestry.command.gettrait", TextFormatting.YELLOW.toString() + trait);
        TextComponentTranslation getCopied = new TextComponentTranslation("bqforestry.command.gettrait.copied");

        if (copy) copyToClipboard(trait);
        player.sendMessage(copy ?
                getTrait.appendSibling(getCopied) :
                getTrait
        );
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
        ValidChromosomes en = getEnumValid(sender);
        ArrayList<String> list = getChromosomes(en);

        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, list);
        }
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
