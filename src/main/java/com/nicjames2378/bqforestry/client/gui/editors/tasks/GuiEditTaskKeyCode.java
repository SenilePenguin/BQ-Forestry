package com.nicjames2378.bqforestry.client.gui.editors.tasks;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.client.gui.misc.IVolatileScreen;
import betterquesting.api.enums.EnumPacketAction;
import betterquesting.api.network.QuestingPacket;
import betterquesting.api.questing.IQuest;
import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.GuiScreenCanvas;
import betterquesting.api2.client.gui.controls.PanelButton;
import betterquesting.api2.client.gui.controls.PanelTextField;
import betterquesting.api2.client.gui.controls.filters.FieldFilterString;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasTextured;
import betterquesting.api2.client.gui.panels.content.PanelLine;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetLine;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.utils.QuestTranslation;
import com.nicjames2378.bqforestry.tasks.TaskKeyCode;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

public class GuiEditTaskKeyCode extends GuiScreenCanvas implements IVolatileScreen {
    private static final ResourceLocation QUEST_EDIT = new ResourceLocation("betterquesting:quest_edit");
    private final IQuest quest;
    private final TaskKeyCode task;

    public GuiEditTaskKeyCode getScreenRef() {
        return this;
    }

    public GuiEditTaskKeyCode(GuiScreen parent, IQuest quest, TaskKeyCode task) {
        super(parent);
        this.quest = quest;
        this.task = task;
    }

    @Override
    public void initPanel() {
        super.initPanel();

        Keyboard.enableRepeatEvents(true);

        //Background
        CanvasTextured cvBackground = new CanvasTextured(new GuiTransform(), PresetTexture.PANEL_MAIN.getTexture());
        this.addPanel(cvBackground);

        // TitleText
        cvBackground.addPanel(new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(16, 16, 16, -32), 0), QuestTranslation.translate("bqforestry.title.edit_bee_retrieval_landing")).setAlignment(1).setColor(PresetColor.TEXT_HEADER.getColor()));

        // Done Button
        cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, -100, -16, 200, 16, 0), -1, QuestTranslation.translate("gui.done")) {
            @Override
            public void onButtonClick() {
                sendChanges();
                mc.displayGuiScreen(parent);
            }
        });

        int width = cvBackground.getTransform().getWidth() / 2;

        cvBackground.addPanel(new PanelTextBox(new GuiTransform(GuiAlign.TOP_LEFT, width - 100, 38, 96, 12, 0), QuestTranslation.translate("bqforestry.label.keycode")).setAlignment(2).setColor(PresetColor.TEXT_MAIN.getColor()));
        cvBackground.addPanel(new PanelTextField<>(new GuiTransform(GuiAlign.TOP_LEFT, width, 32, width - 16, 16, 0), task.keyCode, FieldFilterString.INSTANCE).setCallback(value -> task.keyCode = value));

        // Case Sensitive Button
        String isSensitive = (task.caseSensitive ? TextFormatting.RED : TextFormatting.GREEN) + QuestTranslation.translate(task.caseSensitive ? "gui.yes" : "gui.no");
        PanelButton btnCaseSensitive = new PanelButton(new GuiTransform(GuiAlign.TOP_LEFT, width, 48, width - 16, 16, 0), -1, QuestTranslation.translate("bqforestry.btn.casesensitive", isSensitive)) {
            @Override
            public void onButtonClick() {
                task.caseSensitive = !task.caseSensitive;
                getScreenRef().initGui();
            }
        }
                .setTooltip(RenderUtils.splitString(QuestTranslation.translate("bqforestry.btn.casesensitive.tooltip"), 192, mc.fontRenderer));
        cvBackground.addPanel(btnCaseSensitive);

//region Decorative Elements
        // Top Decorative Line
        IGuiRect ls0 = new GuiTransform(GuiAlign.TOP_LEFT, 16, 32, 0, 0, 0);
        ls0.setParent(cvBackground.getTransform());
        IGuiRect rs0 = new GuiTransform(GuiAlign.TOP_RIGHT, -16, 32, 0, 0, 0);
        rs0.setParent(cvBackground.getTransform());
        PanelLine plTop = new PanelLine(ls0, rs0, PresetLine.GUI_DIVIDER.getLine(), 1, PresetColor.GUI_DIVIDER.getColor(), -1);
        cvBackground.addPanel(plTop);

        // Bottom Decorative Line
        IGuiRect ls1 = new GuiTransform(GuiAlign.BOTTOM_LEFT, 16, -32, 0, 0, 0);
        ls1.setParent(cvBackground.getTransform());
        IGuiRect rs1 = new GuiTransform(GuiAlign.BOTTOM_RIGHT, -16, -32, 0, 0, 0);
        rs1.setParent(cvBackground.getTransform());
        PanelLine plBottom = new PanelLine(ls1, rs1, PresetLine.GUI_DIVIDER.getLine(), 1, PresetColor.GUI_DIVIDER.getColor(), -1);
        cvBackground.addPanel(plBottom);
//endregion
    }

    private void sendChanges() {
        NBTTagCompound base = new NBTTagCompound();
        base.setTag("config", quest.writeToNBT(new NBTTagCompound()));
        base.setTag("progress", quest.writeProgressToNBT(new NBTTagCompound(), null));
        NBTTagCompound tags = new NBTTagCompound();
        tags.setInteger("action", EnumPacketAction.EDIT.ordinal());
        tags.setInteger("questID", QuestingAPI.getAPI(ApiReference.QUEST_DB).getID(quest));
        tags.setTag("data", base);
        QuestingAPI.getAPI(ApiReference.PACKET_SENDER).sendToServer(new QuestingPacket(QUEST_EDIT, tags));
    }
}