package com.nicjames2378.bqforestry.client.gui.editors.panels.templates;

import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.utils.QuestTranslation;
import com.nicjames2378.bqforestry.client.gui.editors.controls.BQButton;
import com.nicjames2378.bqforestry.client.gui.editors.controls.PanelToggleButton;
import com.nicjames2378.bqforestry.client.gui.editors.controls.PanelToggleButton.State;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.abstractions.BQScreenCanvas;
import com.nicjames2378.bqforestry.client.themes.ThemeHandler;
import com.nicjames2378.bqforestry.logic.BigBeeStack;
import forestry.api.apiculture.EnumBeeChromosome;
import net.minecraft.util.Tuple;

import java.util.HashMap;

import static com.nicjames2378.bqforestry.utils.UtilitiesBee.*;

public class TemplateBoolean extends TemplateEmpty {
    private static HashMap<State, Tuple<IGuiTexture, IGuiColor>> stateTexMap = new HashMap<>();
    private String translationTitleKey = "";
    private EnumBeeChromosome chromosome = EnumBeeChromosome.CAVE_DWELLING;
    private State value = State.EITHER;

    public TemplateBoolean(EnumBeeChromosome chromosome, String translationKeyTitle) {
        super(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0));
        this.chromosome = chromosome;
        this.translationTitleKey = translationKeyTitle;

        // https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Colors/Color_picker_tool
        stateTexMap.put(State.ON, new Tuple<>(ThemeHandler.ICON_CHECK_YES.getTexture(), new GuiColorStatic(0xFF00FF00)));
        stateTexMap.put(State.OFF, new Tuple<>(ThemeHandler.ICON_CHECK_NO.getTexture(), new GuiColorStatic(0xFFFF0000)));
        stateTexMap.put(State.EITHER, new Tuple<>(ThemeHandler.ICON_CHECK_BOTH.getTexture(), new GuiColorStatic(0xFFFFFF00)));
    }

    public TemplateBoolean(IGuiRect rect) {
        super(rect);
    }

    protected EnumBeeChromosome getChromosomeValue() {
        return chromosome;
    }

    @Override
    public void initialize(BQScreenCanvas gui, CanvasEmpty canvas) {
        BigBeeStack bee = new BigBeeStack(gui.getSelectedItem());

        // Populate with current stats. Forces [0] because booleans should never have multiple values
        // Also, we will let value keep it's default of EITHER if it doesn't match anything
        String trait = getTrait(gui.getSelectedItem(), chromosome, false)[0];
        for (State state : State.values()) {
            if (trait.equals(state.get())) {
                value = state;
            }
        }

        // Title
        canvas.addPanel(new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(16, 8, 16, -32), 0), QuestTranslation.translate(translationTitleKey)).setAlignment(1).setColor(PresetColor.TEXT_HEADER.getColor()));

        // ToggleBox
        PanelToggleButton btnCheck = new PanelToggleButton(new GuiTransform(GuiAlign.TOP_CENTER, -16, 24, 32, 32, 0), -1, "") {
            @Override
            public void onButtonClick() {
                Tuple<IGuiTexture, IGuiColor> s = stateTexMap.get(incrementToggledStatus());
                setIcon(s.getFirst(), s.getSecond(), 4);
            }
        };
        btnCheck.setToggledStatus(value);
        Tuple<IGuiTexture, IGuiColor> s = stateTexMap.get(btnCheck.getToggledStatus());
        btnCheck.setIcon(s.getFirst(), s.getSecond(), 4);
        canvas.addPanel(btnCheck);

        // Done Button
        BQButton.ConfirmButton doneButton = new BQButton.ConfirmButton(new GuiTransform(GuiAlign.BOTTOM_EDGE, new GuiPadding(4, -28, 4, 4), 0), -1, QuestTranslation.translate("gui.done")) {
            @Override
            public void onButtonClick() {
                // Clear all traits for this chromosome and write only the enabled ones
                clearTraits(bee.getBaseStack(), getChromosomeValue());

                // Set On or Off to their strings, and do nothing otherwise.
                State state = btnCheck.getToggledStatus();
                switch (state) {
                    case ON:
                    case OFF:
                        writeTrait(bee.getBaseStack(), getChromosomeValue(), state.get());
                        break;
                    default:
                        break;
                }
                gui.updateTaskItem(bee);
            }
        };
        canvas.addPanel(doneButton);
    }
}
