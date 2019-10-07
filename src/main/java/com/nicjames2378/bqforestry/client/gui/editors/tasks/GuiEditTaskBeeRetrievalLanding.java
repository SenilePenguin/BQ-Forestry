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
import betterquesting.api2.client.gui.panels.bars.PanelVScrollBar;
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

import static com.nicjames2378.bqforestry.utils.UtilitiesBee.*;

public class GuiEditTaskBeeRetrievalLanding extends GuiScreenCanvas implements IVolatileScreen {
    private static final ResourceLocation QUEST_EDIT = new ResourceLocation("betterquesting:quest_edit");
    private final IQuest quest;
    private final TaskForestryRetrieval task;

    public GuiEditTaskBeeRetrievalLanding getScreenRef() {
        return this;
    }

    public GuiEditTaskBeeRetrievalLanding(GuiScreen parent, IQuest quest, TaskForestryRetrieval task) {
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

        int bgWidth = cvBackground.getTransform().getWidth();

        // AutoConsume Button
        String doAutoConsume = (task.autoConsume ? TextFormatting.RED : TextFormatting.GREEN) + QuestTranslation.translate(task.autoConsume ? "gui.yes" : "gui.no");
        cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.TOP_EDGE, (bgWidth / 3) / 2 - 55, 80, 110, 16, 0), -1, QuestTranslation.translate("bqforestry.btn.autoconsume", doAutoConsume)) {
                    @Override
                    public void onButtonClick() {
                        task.autoConsume = !task.autoConsume;
                        getScreenRef().initGui();
                    }
                }
                        .setTooltip(RenderUtils.splitString(QuestTranslation.translate("bqforestry.btn.autoconsume.tooltip"), 128, mc.fontRenderer))
        );

        // Consume Button
        String doConsume = (task.consume ? TextFormatting.RED : TextFormatting.GREEN) + QuestTranslation.translate(task.consume ? "gui.yes" : "gui.no");
        cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.TOP_EDGE, (bgWidth / 3) / 2 - 55, 80 + 24, 110, 16, 0), -1, QuestTranslation.translate("bqforestry.btn.consume", doConsume)) {
                    @Override
                    public void onButtonClick() {
                        task.consume = !task.consume;
                        getScreenRef().initGui();
                    }
                }
                        .setTooltip(RenderUtils.splitString(QuestTranslation.translate("bqforestry.btn.consume.tooltip"), 128, mc.fontRenderer))
        );

//region List Controls
        // Scroll Container
        CanvasEmpty cvControlsContainer = new CanvasEmpty(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding((bgWidth / 3) - 22, 32, 16, 32), 0)); // Subtract extra width to make icons line up where BQ NBT edit has ## delimiters
        cvBackground.addPanel(cvControlsContainer);

        // List Container
        CanvasScrolling cvButtonsArea = new CanvasScrolling(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0));
        cvControlsContainer.addPanel(cvButtonsArea);

        // RequiredItems List
        int listSize = task.requiredItems.size();
        int areaWidth = cvButtonsArea.getTransform().getWidth();
        for (int i = 0; i <= listSize; i++) {
            if (i != listSize) {
                BigItemStack taskItem = task.requiredItems.get(i);

                cvButtonsArea.addPanel(new PanelGeneric(new GuiRectangle(0, i * 24, 24, 24, -1), new ItemTexture(taskItem)));
                PanelGeneric iconFrame = new PanelGeneric(new GuiRectangle(0, i * 24, 24, 24, 0), PresetTexture.ITEM_FRAME.getTexture());

                iconFrame.setTooltip(getHoverTooltip(taskItem.getBaseStack()));
                //iconFrame.setTooltip(RenderUtils.splitString(getHoverTooltip(taskItem.getBaseStack()), 128, mc.fontRenderer));
                cvButtonsArea.addPanel(iconFrame);

                // getDisplayName() can create a NullPointerException down the stack if the item has an improper Species tag. Nothing I can do except
                // Checking that bee's species is actually in the database before running a getDisplayName() on it should work
                String speciesTrait = getTrait(taskItem.getBaseStack(), EnumBeeChromosome.SPECIES, true)[0];
                String displayName = checkTraitIsInDatabase(EnumBeeChromosome.SPECIES, speciesTrait) ? taskItem.getBaseStack().getDisplayName() : speciesTrait;

                // Task Item button
                PanelButtonStorage<Integer> btnTaskItem = new PanelButtonStorage<>(new GuiRectangle(24, i * 24, areaWidth - 32 - 48/*-icons, buttons, and scrollbar*/, 24, 0), -1, displayName, i);

                btnTaskItem.setCallback(value -> {
                    mc.displayGuiScreen(new GuiEditTaskBeeRetrievalSelection(getScreenRef(), quest, task, value));
                });
                cvButtonsArea.addPanel(btnTaskItem);

                // Delete button
                PanelButtonStorage<Integer> btnDelete = new PanelButtonStorage<>(new GuiRectangle(areaWidth - 8 - 24, i * 24, 24, 24, 0), -1, "x", i);
                btnDelete.setTextHighlight(new GuiColorStatic(128, 128, 128, 255), new GuiColorStatic(255, 0, 0, 255), new GuiColorStatic(255, 0, 0, 255));
                btnDelete.setTooltip(RenderUtils.splitString(
                        QuestTranslation.translate("bqforestry.tooltip.delete") + "\n" + QuestTranslation.translate("bqforestry.tooltip.deleteWarning"), 128, mc.fontRenderer));
                btnDelete.setCallback(value -> {
                    task.requiredItems.remove(task.requiredItems.get(value));
                    getScreenRef().initPanel();
                });
                cvButtonsArea.addPanel(btnDelete);
            }

            // Add New button (happens after loop to ensure we add an extra one, even if all items are deleted)
            PanelButtonStorage<Integer> btnAddNew = new PanelButtonStorage<>(new GuiRectangle(areaWidth - 8 - 48, i * 24, 24, 24, 0), -1, "+", i);
            btnAddNew.setTextHighlight(new GuiColorStatic(128, 128, 128, 255), new GuiColorStatic(0, 255, 0, 255), new GuiColorStatic(0, 255, 0, 255));
            btnAddNew.setTooltip(RenderUtils.splitString(QuestTranslation.translate("bqforestry.tooltip.add"), 128, mc.fontRenderer));
            btnAddNew.setCallback(value -> {
                task.requiredItems.add(value, TaskForestryRetrieval.getDefaultBee());
                getScreenRef().initGui();
            });
            cvButtonsArea.addPanel(btnAddNew);
        }

        // Scrollbar
        PanelVScrollBar scVerBar = new PanelVScrollBar(new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-8, 0, 0, 0), 0));
        scVerBar.setScrollSpeed(ConfigHandler.cfgScrollSpeed);
        cvButtonsArea.setScrollDriverY(scVerBar);
        cvControlsContainer.addPanel(scVerBar);
//endregion

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
}