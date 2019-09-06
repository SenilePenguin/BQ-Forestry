package com.nicjames2378.bqforestry.client.gui.editors.tasks;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.client.gui.misc.IVolatileScreen;
import betterquesting.api.enums.EnumPacketAction;
import betterquesting.api.network.QuestingPacket;
import betterquesting.api.questing.IQuest;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.client.gui.GuiScreenCanvas;
import betterquesting.api2.client.gui.controls.PanelButton;
import betterquesting.api2.client.gui.controls.PanelButtonStorage;
import betterquesting.api2.client.gui.controls.PanelTextField;
import betterquesting.api2.client.gui.controls.filters.FieldFilterString;
import betterquesting.api2.client.gui.misc.*;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.CanvasTextured;
import betterquesting.api2.client.gui.panels.bars.PanelVScrollBar;
import betterquesting.api2.client.gui.panels.content.PanelGeneric;
import betterquesting.api2.client.gui.panels.content.PanelLine;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.resources.textures.ItemTexture;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetLine;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.utils.QuestTranslation;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.canvas.CanvasBeeDatabase;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import com.nicjames2378.bqforestry.tasks.TaskForestryRetrieval;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

import static com.nicjames2378.bqforestry.utils.UtilitiesBee.getBaseBee;

public class GuiEditTaskBeeRetrievalSelection extends GuiScreenCanvas implements IVolatileScreen {
    private final IQuest quest;
    private final TaskForestryRetrieval task;
    private final int indexInList;

    private String selected;

    public GuiEditTaskBeeRetrievalSelection(GuiScreen parent, IQuest quest, TaskForestryRetrieval task, int indexInList) {
        super(parent);
        this.quest = quest;
        this.task = task;
        this.indexInList = indexInList;

        //selected = task.advID;
    }

    @Override
    public void initPanel() {
        super.initPanel();
        Keyboard.enableRepeatEvents(true);

        //Background
        CanvasTextured cvBackground = new CanvasTextured(new GuiTransform(), PresetTexture.PANEL_MAIN.getTexture());
        this.addPanel(cvBackground);

        // TitleText
        cvBackground.addPanel(new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(16, 16, 16, -32), 0), QuestTranslation.translate("bqforestry.title.edit_bee_retrieval_selection")).setAlignment(1).setColor(PresetColor.TEXT_HEADER.getColor()));


//region Left Panel Controls
        // LeftArea
        CanvasEmpty cvLeftArea = new CanvasEmpty(new GuiTransform(GuiAlign.MID_CENTER, new GuiPadding(16, 32, 8, 32), 0));
        cvBackground.addPanel(cvLeftArea);

//endregion


//region Right Panel Controls
        // RightArea
        CanvasEmpty cvRightArea = new CanvasEmpty(new GuiTransform(GuiAlign.HALF_RIGHT, new GuiPadding(8, 32, 16, 32), 0));
        cvBackground.addPanel(cvRightArea);

        // BeeDatabase (Buttons and Icons)
        CanvasBeeDatabase cvBeeDB = new CanvasBeeDatabase(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 16, 8, 0), 1)) {
            private final List<PanelButtonStorage<String>> btnList = new ArrayList<>();

            @Override
            public void refreshSearch() {
                super.refreshSearch();
                btnList.clear();
            }

            @Override
            protected boolean addResult(String entry, int index, int cachedWidth) {
                this.addPanel(new PanelGeneric(new GuiRectangle(0, index * 24, 24, 24, -1), new ItemTexture(new BigItemStack(getBaseBee("forestry.species" + entry)))));
                this.addPanel(new PanelGeneric(new GuiRectangle(0, index * 24, 24, 24, 0), PresetTexture.ITEM_FRAME.getTexture()));

                PanelButtonStorage<String> btnBeeSpecies = new PanelButtonStorage<>(new GuiRectangle(24, index * 24, cachedWidth - 24, 24, 0), -1, entry, "forestry.species" + entry);
                btnBeeSpecies.setActive(!("forestry.species" + entry).equals(selected));
                btnBeeSpecies.setCallback(value -> {
                    selected = value;
                    for (PanelButtonStorage<String> b : btnList) {
                        b.setActive(!b.getStoredValue().equals(selected));
                    }
                });

                this.addPanel(btnBeeSpecies);
                btnList.add(btnBeeSpecies);
                return true;
            }
        };
        cvRightArea.addPanel(cvBeeDB);

        // Search Box
        PanelTextField<String> txtSearch = new PanelTextField<>(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 0, 0, -16), 0), "", FieldFilterString.INSTANCE);
        txtSearch.setCallback(cvBeeDB::setSearchFilter).setWatermark("Search...");
        cvRightArea.addPanel(txtSearch);

        // Scrollbar
        PanelVScrollBar scBeeBar = new PanelVScrollBar(new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-8, 16, 0, 0), 0));
        cvBeeDB.setScrollDriverY(scBeeBar);
        scBeeBar.setScrollSpeed(ConfigHandler.cfgscrollSpeed);
        cvRightArea.addPanel(scBeeBar);
//endregion

        cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, -100, -16, 200, 16, 0), -1, QuestTranslation.translate("gui.done")) {
            @Override
            public void onButtonClick() {
                if (selected != null) sendChanges();
                mc.displayGuiScreen(parent);
            }
        });

        // Midline Divider
        IGuiRect ls0 = new GuiTransform(GuiAlign.TOP_CENTER, 0, 32, 0, 0, 0);
        ls0.setParent(cvBackground.getTransform());
        IGuiRect le0 = new GuiTransform(GuiAlign.BOTTOM_CENTER, 0, -32, 0, 0, 0);
        le0.setParent(cvBackground.getTransform());
        PanelLine paLine0 = new PanelLine(ls0, le0, PresetLine.GUI_DIVIDER.getLine(), 1, PresetColor.GUI_DIVIDER.getColor(), 1);
        cvBackground.addPanel(paLine0);
    }

    private static final ResourceLocation QUEST_EDIT = new ResourceLocation("betterquesting:quest_edit");

    private void sendChanges() {
        task.requiredItems.set(indexInList, task.getBaseBeeBig(selected));
        NBTTagCompound base = new NBTTagCompound();
        base.setTag("config", quest.writeToNBT(new NBTTagCompound()));
        base.setTag("progress", quest.writeProgressToNBT(new NBTTagCompound(), null));
        NBTTagCompound tags = new NBTTagCompound();
        tags.setInteger("action", EnumPacketAction.EDIT.ordinal()); // Action: Update data
        tags.setInteger("questID", QuestingAPI.getAPI(ApiReference.QUEST_DB).getID(quest));
        tags.setTag("data", base);
        QuestingAPI.getAPI(ApiReference.PACKET_SENDER).sendToServer(new QuestingPacket(QUEST_EDIT, tags));
    }
}