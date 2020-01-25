package com.nicjames2378.bqforestry.client.gui.editors.tasks.canvas.panels;

import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.utils.QuestTranslation;
import com.nicjames2378.bqforestry.BQ_Forestry;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.canvas.abstractions.BQScreenCanvas;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.canvas.abstractions.IControlPanel;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.canvas.controls.factory.FactoryForestryDataControlArea;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.canvas.controls.factory.PanelToggleStorage;
import forestry.api.apiculture.EnumBeeChromosome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static com.nicjames2378.bqforestry.utils.StringUtils.capitalizeFirst;
import static com.nicjames2378.bqforestry.utils.StringUtils.indexOfFirstCapital;
import static com.nicjames2378.bqforestry.utils.UtilitiesBee.getAllelesForChromosome;
import static com.nicjames2378.bqforestry.utils.UtilitiesBee.getTrait;

public class ControlSpeed extends CanvasEmpty implements IControlPanel {
    public ControlSpeed(IGuiRect rect) {
        super(rect);
    }

    @Override
    public void initialize(BQScreenCanvas gui, CanvasEmpty canvas) {
        EnumBeeChromosome chromosome = EnumBeeChromosome.SPEED;
        int workingY = 0;


        FactoryForestryDataControlArea dataArea = new FactoryForestryDataControlArea(this, this.getTransform().getWidth() / 2 - 70, workingY, 140);
        dataArea.setTitle(capitalizeFirst(chromosome.getName()), false)
                .setLayout(1, 16)
                .setPanels(getButtonsForChromosome(chromosome, dataArea, gui))
                .buildCanvas();
        workingY += dataArea.getHeight();


        // Done Button
        ConfirmButton doneButton = new ConfirmButton(new GuiTransform(GuiAlign.BOTTOM_EDGE, new GuiPadding(4, -28, 4, 4), 0), -1, QuestTranslation.translate("gui.done")) {
            @Override
            public void onButtonClick() {
                /*
                Need to have each control panel keep a record of valid values.
                Then, when pressing done, check which of these values are toggled.
                This will let us edit the bees in-place instead of recreating from the ground up. It will also let us change single NBT values at a time.
                 */
//                BQ_Forestry.debug(String.format("ControlSpeed: Setting Speed for item #%1$s: - %2$s", gui.getSelectedIndex(), ));

//                writeTrait(bee.getBaseStack(), EnumBeeChromosome.SPECIES, species);
//                gui.updateTaskItem(bee);
            }
        };
        canvas.addPanel(doneButton);
    }

    private IGuiPanel[] getButtonsForChromosome(EnumBeeChromosome chromosome, FactoryForestryDataControlArea factoryProvider, BQScreenCanvas gui) {
        ArrayList<IGuiPanel> panels = new ArrayList<>();

        // Cycle through all alleles for each chromosome
        TreeMap<Integer, String> map = getAllelesForChromosome(chromosome);
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            BQ_Forestry.debug("DEBUG:: " + entry.getValue());

            // Make the new toggle buttons
            PanelToggleStorage<String> newPanel = new PanelToggleStorage<>(factoryProvider.getNextRect(), -1, entry.getValue().substring(indexOfFirstCapital(entry.getValue())), entry.getValue());

            // See if they should be enabled by default or not
            if (Arrays.asList(getTrait(gui.getSelectedItem(), chromosome, false)).contains(newPanel.getStoredValue().toString()))
                newPanel.setToggledStatus(true);

            panels.add(newPanel);
        }
        // Send the panels back for the Factory to use
        return panels.toArray(new IGuiPanel[0]);
    }
}
