package com.nicjames2378.bqforestry.client.gui.editors.panels.templates;

import betterquesting.api2.client.gui.misc.*;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.client.gui.panels.bars.PanelVScrollBar;
import betterquesting.api2.client.gui.panels.lists.CanvasScrolling;
import betterquesting.api2.utils.QuestTranslation;
import com.nicjames2378.bqforestry.BQ_Forestry;
import com.nicjames2378.bqforestry.client.gui.editors.controls.BQButton;
import com.nicjames2378.bqforestry.client.gui.editors.controls.FactoryForestryDataControlArea;
import com.nicjames2378.bqforestry.client.gui.editors.controls.PanelToggleStorage;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.abstractions.BQScreenCanvas;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import com.nicjames2378.bqforestry.logic.BigBeeStack;
import forestry.api.apiculture.EnumBeeChromosome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static com.nicjames2378.bqforestry.utils.StringUtils.capitalizeFirst;
import static com.nicjames2378.bqforestry.utils.StringUtils.indexOfFirstCapital;
import static com.nicjames2378.bqforestry.utils.UtilitiesBee.*;

public class TemplateToggleableList extends TemplateEmpty {
    private EnumBeeChromosome chromosome = EnumBeeChromosome.SPEED;
    private ArrayList<PanelToggleStorage> values = new ArrayList<>();

    protected EnumBeeChromosome getChromosomeValue() {
        return chromosome;
    }

    public TemplateToggleableList(EnumBeeChromosome chromosome) {
        super(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0));
        this.chromosome = chromosome;
    }

    public TemplateToggleableList(IGuiRect rect) {
        super(rect);
    }

    @Override
    public void initialize(BQScreenCanvas gui, CanvasEmpty canvas) {
        BigBeeStack bee = new BigBeeStack(gui.getSelectedItem());

        CanvasScrolling canvasScrolling = new CanvasScrolling(new GuiRectangle(0, 0, canvas.getTransform().getWidth(), canvas.getTransform().getHeight(), 0));//, PresetTexture.QUEST_MAIN_3.getTexture());
        canvas.addPanel(canvasScrolling);

        FactoryForestryDataControlArea dataArea = new FactoryForestryDataControlArea(canvasScrolling, 70/*canvasScrolling.getTransform().getWidth() / 2 - 70*/, 40, 140);
        dataArea.setTitle(capitalizeFirst(getChromosomeValue().getName()), false)
                .setLayout(1, 16)
                .setPanels(getButtonsForChromosome(getChromosomeValue(), dataArea, gui))
                .buildCanvas();

        // Scrollbar
        PanelVScrollBar vScrollBar = new PanelVScrollBar(new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-8, 16, 0, 32), 0));
        canvasScrolling.setScrollDriverY(vScrollBar);
        vScrollBar.setScrollSpeed(ConfigHandler.cfgScrollSpeed);
        canvas.addPanel(vScrollBar);

        // Done Button
        BQButton.ConfirmButton doneButton = new BQButton.ConfirmButton(new GuiTransform(GuiAlign.BOTTOM_EDGE, new GuiPadding(4, -28, 4, 4), 0), -1, QuestTranslation.translate("gui.done")) {
            @Override
            public void onButtonClick() {
                BQ_Forestry.debug(String.format("ControlSpeed: Setting Speed for item #%1$s", gui.getSelectedIndex()));

                // Clear all traits for this chromosome and write only the enabled ones
                clearTraits(bee.getBaseStack(), getChromosomeValue());
                for (PanelToggleStorage panel : values) {
                    if (panel.getToggledStatus()) {
                        writeTrait(bee.getBaseStack(), getChromosomeValue(), panel.getStoredValue().toString());
                    }
                }

                gui.updateTaskItem(bee);
            }
        };
        canvas.addPanel(doneButton);
    }

    private IGuiPanel[] getButtonsForChromosome(EnumBeeChromosome chromosome, FactoryForestryDataControlArea factoryProvider, BQScreenCanvas gui) {
        values = new ArrayList<>();

        // Cycle through all alleles for each chromosome
        TreeMap<Integer, String> map = getAllelesForChromosome(chromosome);
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            // Make the new toggle buttons
            PanelToggleStorage<String> newPanel = new PanelToggleStorage<>(factoryProvider.getNextRect(), -1, entry.getValue().substring(indexOfFirstCapital(entry.getValue())), entry.getValue());

            // See if the bee has the trait and enable by default
            if (Arrays.asList(getTrait(gui.getSelectedItem(), chromosome, false)).contains(newPanel.getStoredValue().toString())) {
                newPanel.setToggledStatus(true);
            }

            values.add(newPanel);
        }
        // Send the panels back for the Factory to use
        return values.toArray(new IGuiPanel[0]);
    }
}
