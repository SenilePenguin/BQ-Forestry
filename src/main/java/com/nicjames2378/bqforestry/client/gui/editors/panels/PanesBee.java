package com.nicjames2378.bqforestry.client.gui.editors.panels;

import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import com.nicjames2378.bqforestry.client.gui.editors.panels.templates.TemplateEmpty;
import com.nicjames2378.bqforestry.client.gui.editors.panels.templates.TemplateToggleableList;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.abstractions.BQScreenCanvas;
import forestry.api.apiculture.EnumBeeChromosome;

@SuppressWarnings("unused") // Stupid IntelliJ being a stupid whiner...
public enum PanesBee {
    // new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0)
    None(new TemplateEmpty(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0))),
    Trash(new PanelTrash(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0))),
    BeeSpecies(new PanelBeeSpecies(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0))),

    BeeSpeed(new TemplateToggleableList(EnumBeeChromosome.SPEED)),
    BeeLifespan(new TemplateToggleableList(EnumBeeChromosome.LIFESPAN)),
    BeeFertility(new TemplateToggleableList(EnumBeeChromosome.FERTILITY)),
    BeeTemperature(new TemplateToggleableList(EnumBeeChromosome.TEMPERATURE_TOLERANCE)),
    BeeSleep(new TemplateToggleableList(EnumBeeChromosome.NEVER_SLEEPS)),
    BeeHumidity(new TemplateToggleableList(EnumBeeChromosome.HUMIDITY_TOLERANCE)),
    BeeRain(new TemplateToggleableList(EnumBeeChromosome.TOLERATES_RAIN)),
    BeeCave(new TemplateToggleableList(EnumBeeChromosome.CAVE_DWELLING)),
    BeeFlowerProvider(new TemplateToggleableList(EnumBeeChromosome.FLOWER_PROVIDER)),
    BeeFloweringSpeed(new TemplateToggleableList(EnumBeeChromosome.FLOWERING)),
    BeeTerritory(new TemplateToggleableList(EnumBeeChromosome.TERRITORY)),
    BeeEffect(new TemplateToggleableList(EnumBeeChromosome.EFFECT));

    private final IPanel canvas;

    PanesBee(final IPanel canvas) {
        this.canvas = canvas;
    }

    public void get(BQScreenCanvas parent, CanvasEmpty container) {
        canvas.initialize(parent, container);
    }
}
