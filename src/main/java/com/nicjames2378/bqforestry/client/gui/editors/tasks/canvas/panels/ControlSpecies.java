package com.nicjames2378.bqforestry.client.gui.editors.tasks.canvas.panels;

import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.client.gui.controls.PanelButtonStorage;
import betterquesting.api2.client.gui.controls.PanelTextField;
import betterquesting.api2.client.gui.controls.filters.FieldFilterString;
import betterquesting.api2.client.gui.misc.*;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.bars.PanelVScrollBar;
import betterquesting.api2.client.gui.panels.content.PanelGeneric;
import betterquesting.api2.client.gui.resources.textures.ItemTexture;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.canvas.CanvasBeeDatabase;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import com.nicjames2378.bqforestry.utils.UtilitiesBee;
import forestry.api.genetics.IAllele;

import java.util.ArrayList;
import java.util.List;

import static com.nicjames2378.bqforestry.utils.UtilitiesBee.getBaseBee;

public class ControlSpecies extends CanvasEmpty implements IControlPanel {

    private final List<PanelButtonStorage<String>> lstSpeciesButtons = new ArrayList<>();
    private String selectedType;

    public String getSelectedType() {
        return selectedType;
    }

    public void setSelectedType(String selectedType) {
        if (selectedType == null) {
            this.selectedType = ConfigHandler.cfgBeeType;
        } else {
            this.selectedType = selectedType;
        }
    }

    public ControlSpecies(IGuiRect rect) {
        super(rect);
    }

    public ControlSpecies initialize(String selectedType) {
        this.selectedType = selectedType;
        return this;
    }

    public ControlSpecies get() {
        // RightArea
        CanvasEmpty cvRightArea = new CanvasEmpty(new GuiTransform(GuiAlign.HALF_RIGHT, new GuiPadding(8, 32, 16, 32), 0));
        addPanel(cvRightArea);

        // BeeDatabase (Buttons and Icons)
        CanvasBeeDatabase cvBeeDB = new CanvasBeeDatabase(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 16, 8, 0), 1)) {
            @Override
            public void refreshSearch() {
                super.refreshSearch();
                lstSpeciesButtons.clear();
            }

            @Override
            protected boolean addResult(IAllele entry, int index, int cachedWidth) {
                String beeUID = entry.getUID();

                this.addPanel(new PanelGeneric(new GuiRectangle(0, index * 24, 24, 24, -1), new ItemTexture(new BigItemStack(getBaseBee(beeUID, UtilitiesBee.BeeTypes.valueOf(getSelectedType()))))));
                this.addPanel(new PanelGeneric(new GuiRectangle(0, index * 24, 24, 24, 0), PresetTexture.ITEM_FRAME.getTexture()));

                PanelButtonStorage<String> btnBeeSpecies = new PanelButtonStorage<>(new GuiRectangle(24, index * 24, cachedWidth - 24, 24, 0), -1, entry.getAlleleName(), entry.getUID());
                btnBeeSpecies.setActive(!beeUID.equals(getSelectedSpecies()));
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
                btnBeeSpecies.setTooltip(getBeeTooltip(entry.getModID(), entry.getUID()));

                this.addPanel(btnBeeSpecies);
                lstSpeciesButtons.add(btnBeeSpecies);
                return true;
            }
        };
        cvRightArea.addPanel(cvBeeDB);

        // Search Box
        PanelTextField<String> txtSearch = new PanelTextField<String>(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 0, 0, -16), 0), "", FieldFilterString.INSTANCE);
        txtSearch.setCallback(cvBeeDB::setSearchFilter).setWatermark("Search...");
        cvRightArea.addPanel(txtSearch);

        // Scrollbar
        PanelVScrollBar scBeeBar = new PanelVScrollBar(new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-8, 16, 0, 0), 0));
        cvBeeDB.setScrollDriverY(scBeeBar);
        scBeeBar.setScrollSpeed(ConfigHandler.cfgScrollSpeed);
        cvRightArea.addPanel(scBeeBar);
    }
}
