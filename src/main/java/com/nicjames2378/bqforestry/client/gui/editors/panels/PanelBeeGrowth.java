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
import com.nicjames2378.bqforestry.client.gui.editors.controls.BQButton;
import com.nicjames2378.bqforestry.client.gui.editors.panels.templates.TemplateEmpty;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.abstractions.BQScreenCanvas;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import com.nicjames2378.bqforestry.logic.BigBeeStack;
import com.nicjames2378.bqforestry.utils.UtilitiesBee;
import forestry.api.apiculture.EnumBeeChromosome;
import net.minecraft.util.text.TextFormatting;

import static com.nicjames2378.bqforestry.utils.UtilitiesBee.*;

public class PanelBeeGrowth extends TemplateEmpty {
    private BeeTypes growthLevel = BeeTypes.valueOf(ConfigHandler.cfgBeeType);

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
        String[] types = UtilitiesBee.getGrowthStages();
        boolean isLeft = true;

        // GrowthLabel
//        canvas.addPanel(new PanelTextBox(new GuiRectangle(lHW - 70, workingY, 140, 12, -1),
//                QuestTranslation.translate("bqforestry.label.beegrowthstage")).setAlignment(1).setColor(PresetColor.TEXT_HEADER.getColor()));
        workingY += 12;

        for (int t = 0; t < types.length; t++) {
            // Text indicator
            String formattedType = (types[t].equals(growthLevel.get()) ? TextFormatting.GREEN : "") + types[t].substring(0, 1).toUpperCase() + types[t].substring(1).toLowerCase();
            canvas.addPanel(new PanelTextBox(new GuiRectangle(lHW + (isLeft ? -66 : 4), (Math.floorDiv(t, 2)) * 64 + 54 + workingY, 70, 16, -1), formattedType));
// TODO: HERE
            // Type Buttons
            PanelButtonStorage<String> btnBeeType = new PanelButtonStorage<>(new GuiRectangle(lHW + (isLeft ? -70 : 0), (Math.floorDiv(t, 2)) * 64 + workingY, 70, 64, 0), -1, "", types[t]);
            btnBeeType.setActive(!(types[t]).equals(growthLevel.get()));
            btnBeeType.setCallback(value -> {
                growthLevel = BeeTypes.valueOf(value);
                // TODO: There has to be a better way to update beeDB icons instead of redrawing the entire screen.... right?
                //
                //      Need to test memory impact of storing all icon panels in array and manually changing the images
                //      vs redrawing the entire screen.
//                gui.initGui();
            });
            btnBeeType.setIcon(new ItemTexture(new BigItemStack(getBaseBee(
                    getTrait(bee.getBaseStack(), EnumBeeChromosome.SPECIES, true)[0],
                    UtilitiesBee.BeeTypes.valueOf(btnBeeType.getStoredValue()))
            )), 8);

            canvas.addPanel(btnBeeType);

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
                gui.updateTaskItem(bee.setType(growthLevel));
            }
        };
        canvas.addPanel(doneButton);
    }
}
