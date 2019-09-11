package com.nicjames2378.bqforestry.client.gui.editors.tasks;

import betterquesting.api.client.gui.misc.IVolatileScreen;
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
import com.nicjames2378.bqforestry.Main;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.canvas.CanvasBeeDatabase;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import com.nicjames2378.bqforestry.tasks.TaskForestryRetrieval;
import com.nicjames2378.bqforestry.utils.UtilitiesBee;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

import static com.nicjames2378.bqforestry.utils.UtilitiesBee.*;

public class GuiEditTaskBeeRetrievalSelection extends GuiScreenCanvas implements IVolatileScreen {
    private final IQuest quest;
    private final TaskForestryRetrieval task;
    private final int indexInList;
    private final GuiEditTaskBeeRetrievalSelection screenReference = this;
    private boolean hasChanged = false;

    private String selectedSpecies;
    private String selectedType;
    private boolean selectedMated;

    //region Getters and Setters
    private String getSelectedSpecies() {
        return selectedSpecies;
    }

    private void setSelectedSpecies(String selectedSpecies) {
        if (selectedSpecies == null) {
            this.selectedSpecies = DEFAULT_SPECIES;
        } else {
            this.selectedSpecies = selectedSpecies;
        }
        hasChanged = true;
    }

    public String getSelectedType() {
        return selectedType;
    }

    public void setSelectedType(String selectedType) {
        if (selectedType == null) {
            this.selectedType = ConfigHandler.cfgBeeType;
        } else {
            this.selectedType = selectedType;
        }
        hasChanged = true;
    }

    private boolean getSelectedMated() {
        return selectedMated;
    }

    private void setSelectedMated(boolean selectedMated) {
        this.selectedMated = selectedMated;
        hasChanged = true;
    }
    //endregion


    public GuiEditTaskBeeRetrievalSelection(GuiScreen parent, IQuest quest, TaskForestryRetrieval task, int indexInList) {
        super(parent);
        this.quest = quest;
        this.task = task;
        this.indexInList = indexInList;

        ItemStack rStack = task.requiredItems.get(indexInList).getBaseStack();

        setSelectedSpecies(getSpecies(rStack));
        setSelectedType(getType(rStack).get());
        setSelectedMated(isMated(rStack));
    }

    @Override
    public void initPanel() {
        super.initPanel();
        Keyboard.enableRepeatEvents(true);
        final List<PanelButtonStorage<String>> lstTypeButtons = new ArrayList<>();
        final List<PanelButtonStorage<String>> lstSpeciesButtons = new ArrayList<>();

        //Background
        CanvasTextured cvBackground = new CanvasTextured(new GuiTransform(), PresetTexture.PANEL_MAIN.getTexture());
        this.addPanel(cvBackground);

        // TitleText
        cvBackground.addPanel(new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(16, 16, 16, -32), 0), QuestTranslation.translate("bqforestry.title.edit_bee_retrieval_selection")).setAlignment(1).setColor(PresetColor.TEXT_HEADER.getColor()));


//region Left Panel Controls
        // LeftArea
        CanvasEmpty cvLeftArea = new CanvasEmpty(new GuiTransform(GuiAlign.HALF_LEFT, new GuiPadding(16, 32, 8, 32), 0));
        cvBackground.addPanel(cvLeftArea);

        int lHW = cvLeftArea.getTransform().getWidth() / 2;
        String[] types = UtilitiesBee.getAllTypes();
        boolean isLeft = true;

        for (int t = 0; t < types.length; t++) {
            // Text indicator
            String formattedType = (types[t].equals(getSelectedType()) ? TextFormatting.GREEN : "") + types[t].substring(0, 1).toUpperCase() + types[t].substring(1).toLowerCase();
            cvLeftArea.addPanel(new PanelTextBox(new GuiRectangle(lHW + (isLeft ? -66 : 4), (Math.floorDiv(t, 2)) * 64 + 54, 70, 16, -1), formattedType));

            // Type Buttons
            PanelButtonStorage<String> btnBeeType = new PanelButtonStorage<>(new GuiRectangle(lHW + (isLeft ? -70 : 0), (Math.floorDiv(t, 2)) * 64, 70, 64, 0), -1, "", types[t]);
            btnBeeType.setActive(!(types[t]).equals(getSelectedType()));
            btnBeeType.setCallback(value -> {
                setSelectedType(value);
                // TODO: There has to be a better way to update beeDB icons instead of redrawing the entire screen.... right?
                screenReference.initGui();
            });
            btnBeeType.setIcon(new ItemTexture(new BigItemStack(getBaseBee(
                    getSelectedSpecies(),
                    UtilitiesBee.BeeTypes.valueOf(btnBeeType.getStoredValue()))
            )), 8);

            cvLeftArea.addPanel(btnBeeType);
            lstTypeButtons.add(btnBeeType);

            // Inverts isLeft
            isLeft ^= true;

            if (t == types.length - 1) { // If we're on the last iteration, add the
                // OnlyMated Button
                PanelButtonStorage<Boolean> btnOnlyMated = new PanelButtonStorage<>(new GuiRectangle(lHW - 70, (Math.floorDiv(t + 1, 2)) * 64, 140, 16, 0), -1, getMatedString(), getSelectedMated());
                btnOnlyMated.setCallback(value -> {
                    setSelectedMated(!value);
                    btnOnlyMated.setStoredValue(!value);
                    btnOnlyMated.setText(getMatedString());
                });

                cvLeftArea.addPanel(btnOnlyMated);
            }
        }
//endregion

//region Right Panel Controls
        // RightArea
        CanvasEmpty cvRightArea = new CanvasEmpty(new GuiTransform(GuiAlign.HALF_RIGHT, new GuiPadding(8, 32, 16, 32), 0));
        cvBackground.addPanel(cvRightArea);

        // BeeDatabase (Buttons and Icons)
        CanvasBeeDatabase cvBeeDB = new CanvasBeeDatabase(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 16, 8, 0), 1)) {
            @Override
            public void refreshSearch() {
                super.refreshSearch();
                lstSpeciesButtons.clear();
            }

            @Override
            protected boolean addResult(String entry, int index, int cachedWidth) {
                // Handle Fixing the Forestry bug
                boolean doForestryFix = false;
                if (ConfigHandler.cfgEnderEndedFix && entry.equals("Ender")) {
                    doForestryFix = true;
                    entry = "Ended";
                }

                this.addPanel(new PanelGeneric(new GuiRectangle(0, index * 24, 24, 24, -1), new ItemTexture(new BigItemStack(getBaseBee("forestry.species" + entry, BeeTypes.valueOf(getSelectedType()))))));
                this.addPanel(new PanelGeneric(new GuiRectangle(0, index * 24, 24, 24, 0), PresetTexture.ITEM_FRAME.getTexture()));

                PanelButtonStorage<String> btnBeeSpecies = new PanelButtonStorage<>(new GuiRectangle(24, index * 24, cachedWidth - 24, 24, 0), -1, (doForestryFix ? "Ender" : entry), "forestry.species" + entry);
                btnBeeSpecies.setActive(!("forestry.species" + entry).equals(getSelectedSpecies()));
                btnBeeSpecies.setCallback(value -> {
                    setSelectedSpecies(value);

                    // Update Species buttons to reflect current selected
                    for (PanelButtonStorage<String> b : lstSpeciesButtons) {
                        b.setActive(!b.getStoredValue().equals(getSelectedSpecies()));
                    }

                    // Update Types buttons to reflect current selected species
                    for (PanelButtonStorage<String> b : lstTypeButtons) {
                        b.setActive(!b.getStoredValue().equals(getSelectedSpecies()));
                        b.setIcon(new ItemTexture(new BigItemStack(getBaseBee(
                                getSelectedSpecies(),
                                UtilitiesBee.BeeTypes.valueOf(b.getStoredValue()))
                        )), 8);
                    }
                });

                this.addPanel(btnBeeSpecies);
                lstSpeciesButtons.add(btnBeeSpecies);
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
        scBeeBar.setScrollSpeed(ConfigHandler.cfgScrollSpeed);
        cvRightArea.addPanel(scBeeBar);
//endregion

        // Done Button
        cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, -100, -16, 200, 16, 0), -1, QuestTranslation.translate("gui.done")) {
            @Override
            public void onButtonClick() {
                Main.log.info(hasChanged);
                task.requiredItems.set(indexInList, new BigItemStack(getBaseBee(getSelectedSpecies(), BeeTypes.valueOf(getSelectedType()), getSelectedMated())));
                //if (hasChanged) sendChanges();
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

    private String getMatedString() {
        String doOnlyMated = (getSelectedMated() ? TextFormatting.RED : TextFormatting.GREEN) + QuestTranslation.translate(getSelectedMated() ? "gui.yes" : "gui.no");
        return QuestTranslation.translate("bqforestry.btn.onlymated", doOnlyMated);
    }
}