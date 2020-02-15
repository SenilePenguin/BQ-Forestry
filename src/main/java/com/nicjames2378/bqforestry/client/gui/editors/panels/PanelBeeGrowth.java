package com.nicjames2378.bqforestry.client.gui.editors.panels;

import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.client.gui.controls.PanelButtonStorage;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.resources.textures.ItemTexture;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.utils.QuestTranslation;
import com.nicjames2378.bqforestry.BQ_Forestry;
import com.nicjames2378.bqforestry.client.gui.editors.controls.BQButton;
import com.nicjames2378.bqforestry.client.gui.editors.panels.templates.TemplateEmpty;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.abstractions.BQScreenCanvas;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import com.nicjames2378.bqforestry.logic.BigBeeStack;
import com.nicjames2378.bqforestry.utils.UtilitiesBee;
import forestry.api.apiculture.EnumBeeChromosome;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

import static com.nicjames2378.bqforestry.utils.UtilitiesBee.*;

public class PanelBeeGrowth extends TemplateEmpty {
    private BeeTypes growthLevel = BeeTypes.valueOf(ConfigHandler.cfgBeeType);
    private final List<PanelButtonStorage<String>> lstGrowthButtons = new ArrayList<>();
    private final List<PanelTextBox> lstGrowthLabels = new ArrayList<>();

    public PanelBeeGrowth() {
        super(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0));
    }

    @Override
    public void initialize(BQScreenCanvas gui, CanvasEmpty canvas) {
        BigBeeStack bee = new BigBeeStack(gui.getSelectedItem());
        growthLevel = getGrowthLevel(bee.getBaseStack());
        // Title
        canvas.addPanel(new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(16, 8, 16, -32), 0), QuestTranslation.translate("bqforestry.label.beegrowthstage")).setAlignment(1).setColor(PresetColor.TEXT_HEADER.getColor()));

        int lHW = canvas.getTransform().getWidth() / 2;
        int workingY = 0;
        boolean isLeft = true;
        String[] types = UtilitiesBee.getGrowthStages();

        workingY += 18;

        for (int t = 0; t < types.length; t++) {
            // Type Button Text
            String formattedType = (types[t].equals(growthLevel.get()) ? TextFormatting.GREEN : "") + types[t].substring(0, 1).toUpperCase() + types[t].substring(1).toLowerCase();
            PanelTextBox lblBeeType = new PanelTextBox(new GuiRectangle(lHW + (isLeft ? -66 : 4), (Math.floorDiv(t, 2)) * 64 + 54 + workingY, 70, 16, -1), formattedType);
            canvas.addPanel(lblBeeType);
            lstGrowthLabels.add(lblBeeType);

            // Type Button
            PanelButtonStorage<String> btnBeeType = new PanelButtonStorage<>(new GuiRectangle(lHW + (isLeft ? -70 : 0), (Math.floorDiv(t, 2)) * 64 + workingY, 70, 64, 0), -1, "", types[t]);
            btnBeeType.setActive(!(types[t]).equals(growthLevel.get()));
            btnBeeType.setCallback(value -> {
                growthLevel = BeeTypes.valueOf(value);
                BQ_Forestry.debug("Selecting: " + growthLevel.get());

                // Update Growth Buttons and Labels to reflect current selection
                for (PanelButtonStorage<String> b : lstGrowthButtons) {
                    String val = b.getStoredValue();
                    if (val.equals(growthLevel.get())) {
                        b.setActive(false);
                        lstGrowthLabels.get(lstGrowthButtons.indexOf(b)).setText(TextFormatting.GREEN + val.substring(0, 1).toUpperCase() + val.substring(1).toLowerCase());
                    } else {
                        b.setActive(true);
                        lstGrowthLabels.get(lstGrowthButtons.indexOf(b)).setText(val.substring(0, 1).toUpperCase() + val.substring(1).toLowerCase());
                    }
                }
            });
            btnBeeType.setIcon(new ItemTexture(new BigItemStack(getBaseBee(
                    getTrait(bee.getBaseStack(), EnumBeeChromosome.SPECIES, true)[0],
                    UtilitiesBee.BeeTypes.valueOf(btnBeeType.getStoredValue()))
            )), 8);

            canvas.addPanel(btnBeeType);
            lstGrowthButtons.add(btnBeeType);

            // Inverts isLeft
            isLeft ^= true;

//            if (t == types.length - 1) { // If we're on the last iteration, add the
//                // OnlyMated Button
//                PanelButtonStorage<Boolean> btnOnlyMated = new PanelButtonStorage<>(new GuiRectangle(lHW - 70, (Math.floorDiv(t + 1, 2)) * 64 + workingY, 140, 16, 0), -1, getMatedString(), getSelectedMated());
//                btnOnlyMated.setCallback(value -> {
//                    setSelectedMated(!value);
//                    btnOnlyMated.setStoredValue(!value);
//                    btnOnlyMated.setText(getMatedString());
//                });
//
//                cvControls.addPanel(btnOnlyMated);
//
//                workingY += (Math.floorDiv(t + 1, 2)) * 64 + 32;
//            }
        }

        // Done Button
        BQButton.ConfirmButton doneButton = new BQButton.ConfirmButton(new GuiTransform(GuiAlign.BOTTOM_EDGE, new GuiPadding(4, -28, 4, 4), 0), -1, QuestTranslation.translate("gui.done")) {
            @Override
            public void onButtonClick() {
                BQ_Forestry.debug(String.format("Setting Bee Growth: Current=%1$s, New=%2$s", bee.getBeeType(), growthLevel));
                gui.updateTaskItem(bee.setType(growthLevel));
            }
        };
        canvas.addPanel(doneButton);
    }
}
