package com.nicjames2378.bqforestry.client.tasks;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.IQuest;
import betterquesting.api2.client.gui.controls.PanelTextField;
import betterquesting.api2.client.gui.controls.filters.FieldFilterString;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.utils.QuestTranslation;
import com.nicjames2378.bqforestry.tasks.TaskKeyCode;
import net.minecraft.client.Minecraft;

public class PanelTaskKeyCode extends CanvasEmpty {
    private final IQuest quest;
    private final TaskKeyCode task;

    public PanelTaskKeyCode(IGuiRect rect, IQuest quest, TaskKeyCode task) {
        super(rect);
        this.quest = quest;
        this.task = task;
    }

    @Override
    public void initPanel() {
        super.initPanel();

        boolean isComplete = task.isComplete(QuestingAPI.getQuestingUUID(Minecraft.getMinecraft().player));
        int width = this.getTransform().getWidth();

        this.addPanel(new PanelTextBox(new GuiTransform(GuiAlign.MID_LEFT, 16, -12, 96, 12, 0), QuestTranslation.translate("bqforestry.label.keycode")).setColor(PresetColor.TEXT_MAIN.getColor()));
        PanelTextField pnlInput = new PanelTextField<>(new GuiTransform(GuiAlign.MID_LEFT, 16, 0, width - 32, 16, 0), "", FieldFilterString.INSTANCE).setCallback(value ->
                task.inputCode = value
        );

        if (isComplete) {
            pnlInput.setText(task.keyCode);
            pnlInput.setActive(false);
        }

        this.addPanel(pnlInput);
    }
}