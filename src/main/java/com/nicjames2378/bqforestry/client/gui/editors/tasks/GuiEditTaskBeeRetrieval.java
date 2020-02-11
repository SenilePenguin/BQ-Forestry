package com.nicjames2378.bqforestry.client.gui.editors.tasks;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.client.gui.misc.IVolatileScreen;
import betterquesting.api.enums.EnumPacketAction;
import betterquesting.api.network.QuestingPacket;
import betterquesting.api.questing.IQuest;
import betterquesting.api.utils.BigItemStack;
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
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.resources.textures.ItemTexture;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetLine;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.utils.QuestTranslation;
import com.nicjames2378.bqforestry.BQ_Forestry;
import com.nicjames2378.bqforestry.client.gui.editors.controls.BQButton;
import com.nicjames2378.bqforestry.client.gui.editors.controls.PanelToggleStorage;
import com.nicjames2378.bqforestry.client.gui.editors.panels.PanesBee;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.abstractions.BQScreenCanvas;
import com.nicjames2378.bqforestry.client.themes.ThemeHandler;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import com.nicjames2378.bqforestry.tasks.TaskForestryRetrieval;
import com.nicjames2378.bqforestry.utils.StringUtils;
import forestry.api.apiculture.EnumBeeChromosome;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.nicjames2378.bqforestry.utils.StringUtils.flattenArray;
import static com.nicjames2378.bqforestry.utils.StringUtils.indexOfFirstCapital;
import static com.nicjames2378.bqforestry.utils.UtilitiesBee.*;

public class GuiEditTaskBeeRetrieval extends BQScreenCanvas implements IVolatileScreen {
    private static final ResourceLocation QUEST_EDIT = new ResourceLocation("betterquesting:quest_edit");
    final HashMap<EnumBeeChromosome, ArrayList<PanelToggleStorage>> mapOptions = new HashMap<>();
    private Tuple<Integer, Integer> _catCurrentCoords = new Tuple<>(8, 8);
    private int catWidthBounds = 0;
    private short catButtonSize = 32;

    private Tuple<Integer, Integer> getCatCoords() {
        Tuple<Integer, Integer> current = _catCurrentCoords;
        int x = _catCurrentCoords.getFirst() + catButtonSize;
        int y = _catCurrentCoords.getSecond();

        if (x > catWidthBounds - 8 - catButtonSize) {
            x = 8;
            y += catButtonSize;
        }

        _catCurrentCoords = new Tuple<>(x, y);
        return current;
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
        _catCurrentCoords = new Tuple<>(8, 8);

        if (task.requiredItems.size() <= 0) setSelectedIndex(getSelectedIndex() - 1);

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
        CanvasScrolling cvBeeScroll = new CanvasScrolling(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(buttonSize, 4, buttonSize, 0), 0));
        cvBeeScrollContainer.addPanel(cvBeeScroll);

        // RequiredItems Buttons
        final List<PanelButtonStorage<Integer>> lstRequiredItemButtons = new ArrayList<>();
        int listSize = task.requiredItems.size();

        for (int i = 0; i <= listSize; i++) {
            if (i != listSize) {
                BigItemStack taskItem = task.requiredItems.get(i);

                // Item Frame icon (aka, button)
                PanelButtonStorage<Integer> btnReqItem = new PanelButtonStorage<>(new GuiRectangle(i * buttonSize + (i * 2), 0, buttonSize, buttonSize, 0), -1, String.valueOf(i), i);
                btnReqItem.setCallback(value -> {
                    setSelectedIndex(value);
                    // Update buttons to reflect current selected
                    for (PanelButtonStorage<Integer> b : lstRequiredItemButtons) {
                        b.setActive(!btnReqItem.getStoredValue().equals(getSelectedIndex()));
                    }
                    refresh();
                });
                btnReqItem.setIcon(btnReqItem.getStoredValue().equals(getSelectedIndex()) ? ThemeHandler.ITEM_FRAME_SELECTED.getTexture() : ThemeHandler.ITEM_FRAME.getTexture());
                btnReqItem.setTooltip(getHoverTooltip(taskItem.getBaseStack(), i));
                btnReqItem.setActive(!btnReqItem.getStoredValue().equals(getSelectedIndex()));

                cvBeeScroll.addPanel(btnReqItem);
                lstRequiredItemButtons.add(btnReqItem);

                // Bee Icon
                // TODO: Figure out bug where bees get wrong icon sometimes. Think it's linked to #16?
                PanelGeneric btnReqItemIcon = new PanelGeneric(new GuiRectangle(i * buttonSize + (i * 2), 2, buttonSize - 2, buttonSize - 2, -1), new ItemTexture(getSafeStack(taskItem)));
                cvBeeScroll.addPanel(btnReqItemIcon);
            }
        }

        // Add New Before Button
        cvBeeScrollContainer.addPanel(new BQButton.AddButton(new GuiRectangle(0, 4, buttonSize, buttonSize, 0), "bqforestry.tooltip.add.left", mc.fontRenderer,
                () -> {
                    task.requiredItems.add(getSelectedIndex(), TaskForestryRetrieval.getDefaultBee());
                    refresh();
                })
        );

        // Add New After Button
        cvBeeScrollContainer.addPanel(new BQButton.AddButton(new GuiRectangle(cvBeeScrollContainer.getTransform().getWidth() - buttonSize, 4, buttonSize, buttonSize, 0), "bqforestry.tooltip.add.right", mc.fontRenderer,
                () -> {
                    if (getSelectedIndex() + 1 >= task.requiredItems.size()) {
                        task.requiredItems.add(TaskForestryRetrieval.getDefaultBee());
                    } else {
                        task.requiredItems.add(getSelectedIndex() + 1, TaskForestryRetrieval.getDefaultBee());
                    }
                    refresh();
                })
        );

        // Scrollbar
        PanelHScrollBar scBeeScrollBarH = new PanelHScrollBar(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(buttonSize + 2, 0, buttonSize + 2, -4), -10));
        scBeeScrollBarH.setScrollSpeed(ConfigHandler.cfgScrollSpeed);
        cvBeeScroll.setScrollDriverX(scBeeScrollBarH);
        cvBeeScrollContainer.addPanel(scBeeScrollBarH);

//        scBeeScrollBarH.setEnabled(cvBeeScroll.getScrollBounds().getHeight() > 0);
//endregion

//region Data Panels
        CanvasEmpty cvDataPanels = new CanvasEmpty(new GuiTransform(GuiAlign.FULL_BOX, 0, 44, cvPanes.getTransform().getWidth(), cvPanes.getTransform().getHeight() - 44, 0));
        cvPanes.addPanel(cvDataPanels);

        int cWidthHalf = cvDataPanels.getTransform().getWidth() / 2;
        int cHeight = cvDataPanels.getTransform().getHeight();
        int cHeightThird = cvDataPanels.getTransform().getHeight() / 3;
//endregion

//region Stats Display Area
        CanvasTextured cvBeeStatsHolder = new CanvasTextured(new GuiTransform(GuiAlign.HALF_LEFT, 0, 0, cWidthHalf - 1, cHeightThird * 2 - 2, 0), PresetTexture.ITEM_FRAME.getTexture());
        cvDataPanels.addPanel(cvBeeStatsHolder);

        CanvasEmpty cvBeeStats = new CanvasEmpty(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(4, 4, 4, 4), 0));
        cvBeeStatsHolder.addPanel(cvBeeStats);

        // TODO: Make this scrollable

        cvBeeStats.addPanel(new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 4, 0, -32), -10), TextFormatting.UNDERLINE.toString() + QuestTranslation.translate("bqforestry.label.beeretrievallabel") + getSelectedIndex()).setFontSize(16).enableShadow(true));
        cvBeeStats.addPanel(new PanelTextBox(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 24, 0, 0), 0), String.join("\n", getBeeInfo(task.requiredItems.get(getSelectedIndex()).getBaseStack()))));
//endregion

//region Category Area
        CanvasTextured cvBeeCategories = new CanvasTextured(new GuiTransform(GuiAlign.HALF_LEFT, 0, cHeightThird * 2, cWidthHalf - 1, cHeightThird + 1, 0), PresetTexture.ITEM_FRAME.getTexture());
        cvDataPanels.addPanel(cvBeeCategories);

        catWidthBounds = cvBeeCategories.getTransform().getWidth();

        // TODO: Make this scrollable

        cvBeeCategories.addPanel(getPanel(PanesBee.Trash, ThemeHandler.ICON_ITEM_REMOVE.getTexture()));
        cvBeeCategories.addPanel(getPanel(PanesBee.BeeGrowth, ThemeHandler.ICON_GENOME_BEE_GROWTH.getTexture()));
        cvBeeCategories.addPanel(/* Species             */ getPanel(PanesBee.BeeSpecies, ThemeHandler.ICON_GENOME_SPECIES.getTexture()));
        cvBeeCategories.addPanel(/* Lifespans           */ getPanel(PanesBee.BeeLifespan, ThemeHandler.ICON_GENOME_LIFESPAN.getTexture()));
        cvBeeCategories.addPanel(/* Production Rates    */ getPanel(PanesBee.BeeSpeed, ThemeHandler.ICON_GENOME_SPEED.getTexture()));
        cvBeeCategories.addPanel(/* Pollination Speeds  */ getPanel(PanesBee.BeeFloweringSpeed, ThemeHandler.ICON_GENOME_FLOWERING.getTexture()));
        cvBeeCategories.addPanel(/* Fertility Rates     */ getPanel(PanesBee.BeeFertility, ThemeHandler.ICON_GENOME_FERTILITY.getTexture()));
        cvBeeCategories.addPanel(/* Territory Sizes     */ getPanel(PanesBee.BeeTerritory, ThemeHandler.ICON_GENOME_TERRITORY.getTexture()));
        cvBeeCategories.addPanel(/* Area Effects        */ getPanel(PanesBee.BeeEffect, ThemeHandler.ICON_GENOME_EFFECT.getTexture()));
        cvBeeCategories.addPanel(/* Climate Tolerances  */ getPanel(PanesBee.BeeTemperature, ThemeHandler.ICON_GENOME_TEMPERATURE_TOLERANCE.getTexture()));
        cvBeeCategories.addPanel(/* Humidity Tolerances */ getPanel(PanesBee.BeeHumidity, ThemeHandler.ICON_GENOME_HUMIDITY_TOLERANCE.getTexture()));
        cvBeeCategories.addPanel(/* Works at Night      */ getPanel(PanesBee.BeeSleep, ThemeHandler.ICON_GENOME_NEVER_SLEEPS.getTexture()));
        cvBeeCategories.addPanel(/* Works in Rain       */ getPanel(PanesBee.BeeRain, ThemeHandler.ICON_GENOME_RAIN_TOLERANCE.getTexture()));
        cvBeeCategories.addPanel(/* Works Underground   */ getPanel(PanesBee.BeeCave, ThemeHandler.ICON_GENOME_CAVE_DWELLING.getTexture()));
        cvBeeCategories.addPanel(/* Suitable Flowers    */ getPanel(PanesBee.BeeFlowerProvider, ThemeHandler.ICON_GENOME_FLOWER_PROVIDER.getTexture()));
//endregion

//region Options Area
        CanvasTextured cvBeeOptions = new CanvasTextured(new GuiTransform(GuiAlign.HALF_RIGHT, 0, 0, cWidthHalf - 1, cHeight, 0), PresetTexture.ITEM_FRAME.getTexture());
        cvDataPanels.addPanel(cvBeeOptions);

        getSelectedOption().get(this, cvBeeOptions);
        BQ_Forestry.debug("Setting Panel to " + getSelectedOption().name());
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

    private PanelButtonStorage<PanesBee> getPanel(PanesBee panel, IGuiTexture icon) {
        Tuple<Integer, Integer> coords = getCatCoords();
        PanelButtonStorage<PanesBee> btnBeePanel = new PanelButtonStorage<>(new GuiTransform(GuiAlign.TOP_LEFT, coords.getFirst(), coords.getSecond(), catButtonSize, catButtonSize, 0), -1, "", panel);
        btnBeePanel.setIcon(icon);
        btnBeePanel.setActive(task.requiredItems.size() > 0);
        btnBeePanel.setCallback(this::setSelectedOption);
        return btnBeePanel;
    }

    private ArrayList<String> getHoverTooltip(ItemStack bee, int index) {
        // Show information about the bee
        // Species: forestry.speciesCommon
        // Type:    Princess
        // Mated:   Yes
        ArrayList<String> tip = new ArrayList<>();
        String GOLD = TextFormatting.GOLD.toString();
        String AQUA = TextFormatting.AQUA.toString();

        // Index
        tip.add((GOLD).concat("Bee Retrieval Item #").concat(String.valueOf(index)));
        // Species
        tip.add((AQUA).concat(getDisplayName(bee)));
        tip.add((AQUA).concat("(" + getTrait(bee, EnumBeeChromosome.SPECIES, true)[0] + ")"));

        return tip;
    }

    private ArrayList<String> getBeeInfo(ItemStack bee) {
        ArrayList<String> info = new ArrayList<>();

        // TODO: #16 Sometimes bees are showing the wrong DisplayNames (but correct species tags?)
        info.add(getInfoString("bqforestry.label.bee.species", bee, EnumBeeChromosome.SPECIES));
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

    private String getInfoString(String translationKey, ItemStack bee, EnumBeeChromosome chromosome) {
        String GOLD = TextFormatting.GOLD.toString();
        String AQUA = TextFormatting.AQUA.toString();
        String DIV = GOLD.concat(", ").concat(AQUA);

        StringUtils.IStringStyle style = (str) -> str.substring(indexOfFirstCapital(str));
        String ret = GOLD.concat(QuestTranslation.translate(translationKey)).concat(": ").concat(AQUA);

        if (chromosome == EnumBeeChromosome.SPECIES) {
            return ret.concat(getDisplayName(bee)).concat(" (" + getTrait(bee, chromosome, true)[0]) + ")";
        }

        return ret.concat(flattenArray(getTrait(bee, chromosome, false), DIV, style));
    }
}