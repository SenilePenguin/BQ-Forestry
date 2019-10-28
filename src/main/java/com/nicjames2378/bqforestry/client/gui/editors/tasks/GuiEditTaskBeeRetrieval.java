package com.nicjames2378.bqforestry.client.gui.editors.tasks;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.client.gui.misc.IVolatileScreen;
import betterquesting.api.enums.EnumPacketAction;
import betterquesting.api.network.QuestingPacket;
import betterquesting.api.questing.IQuest;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.GuiScreenCanvas;
import betterquesting.api2.client.gui.controls.PanelButton;
import betterquesting.api2.client.gui.controls.PanelButtonStorage;
import betterquesting.api2.client.gui.misc.*;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.CanvasTextured;
import betterquesting.api2.client.gui.panels.bars.PanelHScrollBar;
import betterquesting.api2.client.gui.panels.content.PanelGeneric;
import betterquesting.api2.client.gui.panels.content.PanelLine;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.panels.lists.CanvasScrolling;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.textures.ItemTexture;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetLine;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.utils.QuestTranslation;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import com.nicjames2378.bqforestry.tasks.TaskForestryRetrieval;
import forestry.api.apiculture.EnumBeeChromosome;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

import static com.nicjames2378.bqforestry.utils.UtilitiesBee.*;

public class GuiEditTaskBeeRetrieval extends GuiScreenCanvas implements IVolatileScreen {
    private static final ResourceLocation QUEST_EDIT = new ResourceLocation("betterquesting:quest_edit");
    private final IQuest quest;
    private final TaskForestryRetrieval task;

    private int selectedItem = 0;

    public GuiEditTaskBeeRetrieval getScreenRef() {
        return this;
    }

    public GuiEditTaskBeeRetrieval(GuiScreen parent, IQuest quest, TaskForestryRetrieval task) {
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
        cvBackground.addPanel(new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(16, 16, 16, -32), 0), QuestTranslation.translate("bqforestry.title.edit_bee_retrieval")).setAlignment(1).setColor(PresetColor.TEXT_HEADER.getColor()));

        // Done Button
        cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, -100, -16, 200, 16, 0), -1, QuestTranslation.translate("gui.done")) {
            @Override
            public void onButtonClick() {
                sendChanges();
                mc.displayGuiScreen(parent);
            }
        });

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

        CanvasEmpty cvPanes = new CanvasEmpty(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(16, 36, 16, 36), 0));
        cvBackground.addPanel(cvPanes);

//region Bees Scroll Area
        // Scroll Area Container
        CanvasEmpty cvBeeScrollContainer = new CanvasEmpty(new GuiTransform(GuiAlign.TOP_LEFT, 0, 0, cvPanes.getTransform().getWidth(), 42, 0));
        cvPanes.addPanel(cvBeeScrollContainer);
        int buttonSize = cvBeeScrollContainer.getTransform().getHeight() - 4;

        // Scroll Area
        CanvasScrolling cvBeeScroll = new CanvasScrolling(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 4, buttonSize, 0), 0));
        cvBeeScrollContainer.addPanel(cvBeeScroll);

        // RequiredItems Buttons
        final List<PanelButtonStorage<Integer>> lstRequiredItemButtons = new ArrayList<>();
        int listSize = task.requiredItems.size();

        for (int i = 0; i <= listSize; i++) {
            if (i != listSize) {
                BigItemStack taskItem = task.requiredItems.get(i);

                // Item Frame icon (aka, button)
                PanelButtonStorage<Integer> btnReqItem = new PanelButtonStorage<>(new GuiRectangle(i * buttonSize + (i * 2), 0, buttonSize, buttonSize, 0), -1, String.valueOf(i), i);
                btnReqItem.setIcon(PresetTexture.ITEM_FRAME.getTexture());
                btnReqItem.setTooltip(getHoverTooltip(taskItem.getBaseStack()));
                btnReqItem.setActive(!btnReqItem.getStoredValue().equals(selectedItem));
                btnReqItem.setCallback(value -> {
                    selectedItem = value;
                    // Update buttons to reflect current selected
                    for (PanelButtonStorage<Integer> b : lstRequiredItemButtons) {
                        b.setActive(!btnReqItem.getStoredValue().equals(selectedItem));
                    }
                    refresh();
                });

                cvBeeScroll.addPanel(btnReqItem);
                lstRequiredItemButtons.add(btnReqItem);
//                PanelGeneric iconFrame = new PanelGeneric(new GuiRectangle(i * buttonSize + (i * 2), 0, buttonSize, buttonSize, 0), PresetTexture.ITEM_FRAME.getTexture());
//                iconFrame.setTooltip(getHoverTooltip(taskItem.getBaseStack()));
//                cvBeeScroll.addPanel(iconFrame);

                // Bee Icon
                PanelGeneric btnReqItemIcon = new PanelGeneric(new GuiRectangle(i * buttonSize + (i * 2), 2, buttonSize - 2, buttonSize - 2, -1), new ItemTexture(getSafeStack(taskItem)));
                cvBeeScroll.addPanel(btnReqItemIcon);
            } else {
                // AddNew button
                PanelButton btnAddNew = new PanelButton(new GuiRectangle(cvBeeScrollContainer.getTransform().getWidth() - buttonSize, 4, buttonSize, buttonSize, 0), -1, "+") {
                    @Override
                    public void onButtonClick() {
                        task.requiredItems.add(TaskForestryRetrieval.getDefaultBee());
                        refresh();
                    }
                };
                btnAddNew.setTextHighlight(new GuiColorStatic(128, 128, 128, 255), new GuiColorStatic(0, 255, 0, 255), new GuiColorStatic(0, 255, 0, 255));
                btnAddNew.setTooltip(RenderUtils.splitString(QuestTranslation.translate("bqforestry.tooltip.add"), 128, mc.fontRenderer));

                cvBeeScrollContainer.addPanel(btnAddNew);
            }
        }

        // Scrollbar
        PanelHScrollBar scBeeScrollBarH = new PanelHScrollBar(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 0, buttonSize + 2, -4), -10));
        scBeeScrollBarH.setScrollSpeed(ConfigHandler.cfgScrollSpeed);
        cvBeeScroll.setScrollDriverX(scBeeScrollBarH);
        cvBeeScrollContainer.addPanel(scBeeScrollBarH);

        scBeeScrollBarH.setEnabled(cvBeeScroll.getScrollBounds().getHeight() > 0);
//endregion

//region Data Panels
        CanvasEmpty cvDataPanels = new CanvasEmpty(new GuiTransform(GuiAlign.FULL_BOX, 0, 44, cvPanes.getTransform().getWidth(), cvPanes.getTransform().getHeight() - 44, 0));
        cvPanes.addPanel(cvDataPanels);

        int cWidthHalf = cvDataPanels.getTransform().getWidth() / 2;
        int cHeight = cvDataPanels.getTransform().getHeight();
//        int cHeightHalf = cvDataPanels.getTransform().getHeight() / 2;
        int cHeightThird = cvDataPanels.getTransform().getHeight() / 3;
//endregion

//region Stats Display Area
        CanvasTextured cvBeeStats = new CanvasTextured(new GuiTransform(GuiAlign.HALF_LEFT, 0, 0, cWidthHalf - 1, cHeightThird * 2 - 2, 0), PresetTexture.ITEM_FRAME.getTexture());
        cvDataPanels.addPanel(cvBeeStats);
//endregion

//region Category Area
        CanvasTextured cvBeeCategories = new CanvasTextured(new GuiTransform(GuiAlign.HALF_LEFT, 0, cHeightThird * 2, cWidthHalf - 1, cHeightThird + 1, 0), PresetTexture.ITEM_FRAME.getTexture());
        cvDataPanels.addPanel(cvBeeCategories);
//endregion

//region Options Area
        CanvasTextured cvBeeOptions = new CanvasTextured(new GuiTransform(GuiAlign.HALF_RIGHT, 0, 0, cWidthHalf - 1, cHeight, 0), PresetTexture.ITEM_FRAME.getTexture());
        cvDataPanels.addPanel(cvBeeOptions);
//endregion
    }

    private void sendChanges() {
        NBTTagCompound base = new NBTTagCompound();
        base.setTag("config", quest.writeToNBT(new NBTTagCompound()));
        base.setTag("progress", quest.writeProgressToNBT(new NBTTagCompound(), null));
        NBTTagCompound tags = new NBTTagCompound();
        tags.setInteger("action", EnumPacketAction.EDIT.ordinal()); // Action: Update data
        tags.setInteger("questID", QuestingAPI.getAPI(ApiReference.QUEST_DB).getID(quest));
        tags.setTag("data", base);
        QuestingAPI.getAPI(ApiReference.PACKET_SENDER).sendToServer(new QuestingPacket(QUEST_EDIT, tags));
    }

    private void refresh() {
        getScreenRef().initGui();
    }

    private ArrayList<String> getHoverTooltip(ItemStack bee) {
        // Show information about the bee
        // Species: forestry.speciesCommon
        // Type:    Princess
        // Mated:   Yes
        ArrayList<String> tip = new ArrayList<>();
        String GOLD = TextFormatting.GOLD.toString();
        String AQUA = TextFormatting.AQUA.toString();

        // Species
        tip.add(GOLD.concat("Species: ").concat(AQUA).concat(getTrait(bee, EnumBeeChromosome.SPECIES, true)[0]));
        // Type
        tip.add(GOLD.concat("Type: ").concat(AQUA).concat(getGrowthLevel(bee).get()));
        // Mated
        tip.add(GOLD.concat("Mated: ").concat(AQUA).concat(String.valueOf(isMated(bee))));

        return tip;
    }
}