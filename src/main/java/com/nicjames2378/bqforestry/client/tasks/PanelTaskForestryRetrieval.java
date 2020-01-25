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
import com.nicjames2378.bqforestry.tasks.TaskForestryRetrieval;
import com.nicjames2378.bqforestry.utils.UtilitiesBee;
import mezz.jei.Internal;
import mezz.jei.api.recipe.IFocus.Mode;
import mezz.jei.gui.Focus;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Optional.Method;

import java.util.ArrayList;
import java.util.UUID;

public class PanelTaskForestryRetrieval extends CanvasEmpty {
    private final TaskForestryRetrieval task;

    public PanelTaskForestryRetrieval(IGuiRect rect, TaskForestryRetrieval task) {
        super(rect);
        this.task = task;
    }

    @Override
    public void initPanel() {
        super.initPanel();

        UUID uuid = QuestingAPI.getQuestingUUID(Minecraft.getMinecraft().player);
        int[] progress = task.getUsersProgress(uuid);
        boolean isComplete = task.isComplete(uuid);

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

        for (int i = 0; i < task.requiredItems.size(); i++) {
            BigItemStack stack = task.requiredItems.get(i);

            // Have to do some trickery here to ensure we don't get a crash for non Forestry-certified items. Smh
            BigItemStack safeStack = UtilitiesBee.getSafeStack(stack);

            // ItemSlot
            PanelItemSlot slot = new PanelItemSlot(new GuiRectangle(0, i * 38, 32, 32, 0), -1, safeStack, false, true);
            ArrayList<String> tTip = new ArrayList<>();
            tTip.add("AAA");
            slot.setTooltip(tTip);
            if (BQ_Forestry.hasJEI) slot.setCallback(value -> lookupRecipe(value.getBaseStack()));
            cvList.addPanel(slot);

            StringBuilder sb = new StringBuilder();

            // Name
            sb.append(safeStack.getBaseStack().getDisplayName());

            // Requirements
            int reqs = 0;
            sb.append("\n").append(TextFormatting.GOLD).append("Requirements:").append(TextFormatting.RESET);
            reqs += tryAddRequirement(sb, reqs, UtilitiesBee.isMated(stack.getBaseStack()), QuestTranslation.translate("bqforestry.requirements.mated"));
            if (reqs == 0) sb.append(" None");

            // Completion Status
            sb.append("\n").append(progress[i]).append("/").append(stack.stackSize).append(" - ");
            if (isComplete || progress[i] >= stack.stackSize) {
                sb.append(TextFormatting.GREEN).append(QuestTranslation.translate("betterquesting.tooltip.complete"));
            } else {
                sb.append(TextFormatting.RED).append(QuestTranslation.translate("betterquesting.tooltip.incomplete"));
            }

            PanelTextBox text = new PanelTextBox(new GuiRectangle(36, i * 38 + 2, listW - 36, 40, 0), sb.toString());
            text.setColor(PresetColor.TEXT_MAIN.getColor());
            cvList.addPanel(text);
        }
    }

    private int tryAddRequirement(StringBuilder sb, int requirementAmount, boolean value, String text) {
        if (value) {
            sb.append(requirementAmount > 0 ? ", " : " ").append(text);
            return 1;
        }
        return 0;
    }

    @Method(modid = "jei")
    private void lookupRecipe(ItemStack stack) {
        if (stack == null || stack.isEmpty() || Internal.getRuntime() == null) return;
        Internal.getRuntime().getRecipesGui().show(new Focus<>(Mode.OUTPUT, stack));
    }
}