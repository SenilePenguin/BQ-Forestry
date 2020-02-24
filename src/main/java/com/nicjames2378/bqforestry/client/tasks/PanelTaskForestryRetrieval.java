package com.nicjames2378.bqforestry.client.tasks;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.client.gui.misc.*;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.bars.PanelVScrollBar;
import betterquesting.api2.client.gui.panels.content.PanelItemSlot;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.panels.lists.CanvasScrolling;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.utils.QuestTranslation;
import com.nicjames2378.bqforestry.BQ_Forestry;
import com.nicjames2378.bqforestry.logic.BigBeeStack;
import com.nicjames2378.bqforestry.tasks.TaskForestryRetrieval;
import com.nicjames2378.bqforestry.utils.StringUtils;
import com.nicjames2378.bqforestry.utils.UtilitiesBee;
import forestry.api.apiculture.EnumBeeChromosome;
import mezz.jei.Internal;
import mezz.jei.api.recipe.IFocus.Mode;
import mezz.jei.gui.Focus;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Optional.Method;

import java.util.ArrayList;
import java.util.UUID;

import static com.nicjames2378.bqforestry.utils.StringUtils.flattenArray;
import static com.nicjames2378.bqforestry.utils.StringUtils.indexOfFirstCapital;
import static com.nicjames2378.bqforestry.utils.UtilitiesBee.getDisplayName;
import static com.nicjames2378.bqforestry.utils.UtilitiesBee.getTrait;

public class PanelTaskForestryRetrieval extends CanvasEmpty {
    private final TaskForestryRetrieval task;

    public PanelTaskForestryRetrieval(IGuiRect rect, TaskForestryRetrieval task) {
        super(rect);
        this.task = task;
    }

    @Override
    public void initPanel() {
        super.initPanel();

        // Consume indicator
        String doConsume = (task.consume ? TextFormatting.RED : TextFormatting.GREEN) + QuestTranslation.translate(task.consume ? "gui.yes" : "gui.no");
        this.addPanel(new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 0, 0, -16), 1), QuestTranslation.translate("bqforestry.btn.consume", doConsume)).setColor(PresetColor.TEXT_MAIN.getColor()));

        // Scroll Container
        CanvasScrolling cvList = new CanvasScrolling(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 16, 8, 0), 0));
        this.addPanel(cvList);

        // Scrollbar
        PanelVScrollBar scList = new PanelVScrollBar(new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-8, 16, 0, 0), 0));
        this.addPanel(scList);
        cvList.setScrollDriverY(scList);

        int listW = cvList.getTransform().getWidth();
        int currentY = 38;

        if (task.requiredItems.size() > 0) {
            for (int i = 0; i < task.requiredItems.size(); i++) {
                BigBeeStack beeStack = new BigBeeStack(task.requiredItems.get(i));
                // Have to do some trickery here to ensure we don't get a crash for non Forestry-certified items. Smh
                BigItemStack safeStack = UtilitiesBee.getSafeStack(task.requiredItems.get(i));

                String info = getInfo(beeStack, i);
                // Add 1 for the last line
                int lines = 1 + StringUtils.getCount(info, "\n");
                // And additional lines for buffer space, then multiply by text height (9)
                int height = (lines + 2) * 9;
                StringUtils.IStringStyle style = (str) -> str.replaceAll("%%1", TextFormatting.GOLD.toString()).replaceAll("%%2", TextFormatting.AQUA.toString());

                // ItemSlot
                PanelItemSlot slot = new PanelItemSlot(new GuiRectangle(0, currentY, 32, 32, 0), -1, safeStack, false, true);
                if (BQ_Forestry.hasJEI) slot.setCallback(value -> lookupRecipe(value.getBaseStack()));
                cvList.addPanel(slot);

                // Use 2px offset to make things look prettier
                PanelTextBox text = new PanelTextBox(new GuiRectangle(36, currentY + 2, listW - 36, height - 2, 0), style.stylize(info));
                text.setColor(PresetColor.TEXT_MAIN.getColor());
                cvList.addPanel(text);

                currentY += height;
            }
        } else {
            PanelTextBox text = new PanelTextBox(new GuiRectangle(36, currentY + 2, listW - 36, 40, 0), "No  Quest Items Found!");
            text.setColor(PresetColor.TEXT_MAIN.getColor()).setFontSize(12);
            cvList.addPanel(text);
        }
    }

    private static String getInfoString(String translationKey, ItemStack bee, EnumBeeChromosome chromosome) {
        String GOLD = "%%1";//TextFormatting.GOLD.toString();
        String AQUA = "%%2";//TextFormatting.AQUA.toString();
        String DIV = GOLD.concat(", ").concat(AQUA);

        StringUtils.IStringStyle style = (str) -> str.substring(indexOfFirstCapital(str));
        String ret = GOLD.concat(QuestTranslation.translate(translationKey)).concat(": ").concat(AQUA);

        if (chromosome == EnumBeeChromosome.SPECIES) {
            return ret.concat(getDisplayName(bee)).concat(" (" + getTrait(bee, chromosome, true)[0]) + ")";
        }

        return ret.concat(flattenArray(getTrait(bee, chromosome, false), DIV, style));
    }

    private ArrayList<String> getInfoList(ItemStack bee) {
        ArrayList<String> info = new ArrayList<>();

        //info.add(getInfoString("bqforestry.label.bee.species", bee, EnumBeeChromosome.SPECIES));
        info.add(getInfoString("bqforestry.label.bee.lifespan", bee, EnumBeeChromosome.LIFESPAN));
        info.add(getInfoString("bqforestry.label.bee.speeds", bee, EnumBeeChromosome.SPEED));
        info.add(getInfoString("bqforestry.label.bee.flowering", bee, EnumBeeChromosome.FLOWERING));
        info.add(getInfoString("bqforestry.label.bee.fertility", bee, EnumBeeChromosome.FERTILITY));
        info.add(getInfoString("bqforestry.label.bee.territory", bee, EnumBeeChromosome.TERRITORY));
        info.add(getInfoString("bqforestry.label.bee.effect", bee, EnumBeeChromosome.EFFECT));
        info.add(getInfoString("bqforestry.label.bee.temp", bee, EnumBeeChromosome.TEMPERATURE_TOLERANCE));
        info.add(getInfoString("bqforestry.label.bee.humidity", bee, EnumBeeChromosome.HUMIDITY_TOLERANCE));
        info.add(getInfoString("bqforestry.label.bee.sleeps", bee, EnumBeeChromosome.NEVER_SLEEPS));
        info.add(getInfoString("bqforestry.label.bee.rain", bee, EnumBeeChromosome.TOLERATES_RAIN));
        info.add(getInfoString("bqforestry.label.bee.dwelling", bee, EnumBeeChromosome.CAVE_DWELLING));
        info.add(getInfoString("bqforestry.label.bee.flowers", bee, EnumBeeChromosome.FLOWER_PROVIDER));
        return info;
    }

    private String getInfo(BigBeeStack stack, int index) {
        UUID uuid = QuestingAPI.getQuestingUUID(Minecraft.getMinecraft().player);
        int[] progress = task.getUsersProgress(uuid);
        boolean isComplete = task.isComplete(uuid);
        StringBuilder sb = new StringBuilder();

        // Name
        // Have to do some trickery here to ensure we don't get a crash for non Forestry-certified items. Smh
        sb.append(UtilitiesBee.getSafeStack(stack).getBaseStack().getDisplayName());

        // Completion Status (Commented out section is for amounts. May not be needed?)
        sb.append(" - ")/*append(progress[index]).append("/").append(stack.stackSize).append(" - ")*/;
        if (isComplete || progress[index] >= stack.stackSize) {
            sb.append(TextFormatting.GREEN).append(QuestTranslation.translate("betterquesting.tooltip.complete"));
        } else {
            sb.append(TextFormatting.RED).append(QuestTranslation.translate("betterquesting.tooltip.incomplete"));
        }

        // Requirements
        sb.append("\n").append(TextFormatting.GOLD).append("Requirements:\n").append(TextFormatting.RESET);

        ArrayList<String> infoList = getInfoList(stack.getBaseStack());
        int numLeft = infoList.size();
        for (String s : infoList) {

            // If it doesn't contain the "Any", add it. Else decrement a count.
            if (!s.toLowerCase().contains("%%2Any".toLowerCase())) {
                sb.append("   ").append(s).append("\n");
            } else {
                numLeft -= 1;
            }
        }

        if (numLeft == 0) sb.append("   ").append("%%2None").append("\n");

        return sb.toString();
    }

    @Method(modid = "jei")
    private void lookupRecipe(ItemStack stack) {
        if (stack == null || stack.isEmpty() || Internal.getRuntime() == null) return;
        Internal.getRuntime().getRecipesGui().show(new Focus<>(Mode.OUTPUT, stack));
    }
}