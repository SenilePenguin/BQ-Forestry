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
    BeeLifespan(new TemplateToggleableList(EnumBeeChromosome.LIFESPAN, "bqforestry.label.bee.lifespan")),
    BeeSpeed(new TemplateToggleableList(EnumBeeChromosome.SPEED, "bqforestry.label.bee.speeds")),
    BeeFloweringSpeed(new TemplateToggleableList(EnumBeeChromosome.FLOWERING, "bqforestry.label.bee.flowering")),
    BeeFertility(new TemplateToggleableList(EnumBeeChromosome.FERTILITY, "bqforestry.label.bee.fertility")),
    BeeTerritory(new TemplateToggleableList(EnumBeeChromosome.TERRITORY, "bqforestry.label.bee.territory")),
    BeeEffect(new TemplateToggleableList(EnumBeeChromosome.EFFECT, "bqforestry.label.bee.effect")),
    BeeTemperature(new TemplateToggleableList(EnumBeeChromosome.TEMPERATURE_TOLERANCE, "bqforestry.label.bee.temp")),
    BeeHumidity(new TemplateToggleableList(EnumBeeChromosome.HUMIDITY_TOLERANCE, "bqforestry.label.bee.humidity")),
    BeeSleep(new TemplateToggleableList(EnumBeeChromosome.NEVER_SLEEPS, "bqforestry.label.bee.sleeps")),
    BeeRain(new TemplateToggleableList(EnumBeeChromosome.TOLERATES_RAIN, "bqforestry.label.bee.rain")),
    BeeCave(new TemplateToggleableList(EnumBeeChromosome.CAVE_DWELLING, "bqforestry.label.bee.dwelling")),
    BeeFlowerProvider(new TemplateToggleableList(EnumBeeChromosome.FLOWER_PROVIDER, "bqforestry.label.bee.flowers"));

    // TODO: Implement changing of the "type" or "growth" of the bees. This was an oversight...
    // TODO: Make another template for True/False items since there's really no need for a while list when a checkbox will work.
    // TODO: Can we make the flower providers show some of the applicable blocks? Could be interesting...
    // TODO: How can we make the temp and humidity tolerances look nicer and easier to understand?
    // TODO: Need a visual for territory? More visual all around!!

    private final IPanel canvas;

    PanesBee(final IPanel canvas) {
        this.canvas = canvas;
    }

    public void get(BQScreenCanvas parent, CanvasEmpty container) {
        canvas.initialize(parent, container);
    }
}
