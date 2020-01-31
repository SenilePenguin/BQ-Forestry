package com.nicjames2378.bqforestry.client.gui.editors.panels;

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
import betterquesting.api2.utils.QuestTranslation;
import com.nicjames2378.bqforestry.BQ_Forestry;
import com.nicjames2378.bqforestry.client.gui.editors.controls.BQButton;
import com.nicjames2378.bqforestry.client.gui.editors.panels.canvas.CanvasBeeDatabase;
import com.nicjames2378.bqforestry.client.gui.editors.panels.templates.TemplateEmpty;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.abstractions.BQScreenCanvas;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import com.nicjames2378.bqforestry.logic.BigBeeStack;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.genetics.IAllele;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

import static com.nicjames2378.bqforestry.utils.UtilitiesBee.*;

public class PanelBeeSpecies extends TemplateEmpty {

    private final List<PanelButtonStorage<String>> lstSpeciesButtons = new ArrayList<>();
    private String species = DEFAULT_SPECIES;

    public PanelBeeSpecies(IGuiRect rect) {
        super(rect);
    }

    @Override
    public void initialize(BQScreenCanvas gui, CanvasEmpty canvas) {
        species = getTrait(gui.getSelectedItem(), EnumBeeChromosome.SPECIES, true)[0];
        BigBeeStack bee = new BigBeeStack(gui.getSelectedItem());

        CanvasBeeDatabase cvBeeDB = new CanvasBeeDatabase(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 16, 8, 32), 1)) {
            @Override
            public void refreshSearch() {
                super.refreshSearch();
//                lstSpeciesButtons.clear();
            }

            @Override
            protected boolean addResult(IAllele entry, int index, int cachedWidth) {
                String beeUID = entry.getUID();

                this.addPanel(new PanelGeneric(new GuiRectangle(0, index * 24, 24, 24, -1), new ItemTexture(new BigItemStack(getBaseBee(beeUID, bee.beeType)))));
                this.addPanel(new PanelGeneric(new GuiRectangle(0, index * 24, 24, 24, 0), PresetTexture.ITEM_FRAME.getTexture()));

                PanelButtonStorage<String> btnBeeSpecies = new PanelButtonStorage<>(new GuiRectangle(24, index * 24, cachedWidth - 24, 24, 0), -1, entry.getAlleleName(), entry.getUID());
                btnBeeSpecies.setActive(!beeUID.equals(species));
                btnBeeSpecies.setCallback(value -> {
                    species = value;

                    // Update Species buttons to reflect current selected
                    for (PanelButtonStorage<String> b : lstSpeciesButtons) {
                        b.setActive(!b.getStoredValue().equals(species));
                    }
                });
                btnBeeSpecies.setTooltip(getBeeTooltip(entry.getModID(), entry.getUID()));

                this.addPanel(btnBeeSpecies);
                lstSpeciesButtons.add(btnBeeSpecies);
                return true;
            }
        };
        canvas.addPanel(cvBeeDB);

        // Search Box
        PanelTextField<String> txtSearch = new PanelTextField<String>(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 0, 0, -18), 0), "", FieldFilterString.INSTANCE);
        txtSearch.setCallback(cvBeeDB::setSearchFilter).setWatermark("Search...");
        canvas.addPanel(txtSearch);

        // Scrollbar
        PanelVScrollBar scBeeBar = new PanelVScrollBar(new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-8, 16, 0, 32), 0));
        cvBeeDB.setScrollDriverY(scBeeBar);
        scBeeBar.setScrollSpeed(ConfigHandler.cfgScrollSpeed);
        canvas.addPanel(scBeeBar);

        // Done Button
        BQButton.ConfirmButton doneButton = new BQButton.ConfirmButton(new GuiTransform(GuiAlign.BOTTOM_EDGE, new GuiPadding(4, -28, 4, 4), 0), -1, QuestTranslation.translate("gui.done")) {
            @Override
            public void onButtonClick() {
                BQ_Forestry.debug(String.format("ControlSpecies: Setting Species for item #%1$s: - %2$s", gui.getSelectedIndex(), species));

                writeTrait(bee.getBaseStack(), EnumBeeChromosome.SPECIES, species);
                gui.updateTaskItem(bee);
            }
        };
        canvas.addPanel(doneButton);
    }

    private List<String> getBeeTooltip(String modID, String uid) {
        ArrayList<String> ret = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        sb.append(TextFormatting.GOLD).append("ModID: ").append(TextFormatting.AQUA).append(modID);
        ret.add(sb.toString());

        sb.delete(0, sb.length()); // Reuse StringBuilder instead of instantiating new one for this little bit

        sb.append(TextFormatting.GOLD).append("UID: ").append(TextFormatting.AQUA).append(uid);
        ret.add(sb.toString());
        return ret;
    }
}
