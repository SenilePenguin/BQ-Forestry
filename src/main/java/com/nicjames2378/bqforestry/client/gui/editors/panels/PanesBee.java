package com.nicjames2378.bqforestry.client.gui.editors.panels;

import betterquesting.api2.client.gui.panels.CanvasEmpty;
import com.nicjames2378.bqforestry.client.gui.editors.panels.templates.TemplateBoolean;
import com.nicjames2378.bqforestry.client.gui.editors.panels.templates.TemplateEmpty;
import com.nicjames2378.bqforestry.client.gui.editors.panels.templates.TemplateToggleableChromosomes;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.abstractions.BQScreenCanvas;
import forestry.api.apiculture.EnumBeeChromosome;

@SuppressWarnings("unused") // Stupid IntelliJ being a stupid whiner...
public enum PanesBee {
    // new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0)
    None(new TemplateEmpty()),
    Trash(new PanelTrash()),
    BeeSpecies(new PanelBeeSpecies()),
    BeeLifespan(new TemplateToggleableChromosomes(EnumBeeChromosome.LIFESPAN, "bqforestry.label.bee.lifespan")),
    BeeSpeed(new TemplateToggleableChromosomes(EnumBeeChromosome.SPEED, "bqforestry.label.bee.speeds")),
    BeeFloweringSpeed(new TemplateToggleableChromosomes(EnumBeeChromosome.FLOWERING, "bqforestry.label.bee.flowering")),
    BeeFertility(new TemplateToggleableChromosomes(EnumBeeChromosome.FERTILITY, "bqforestry.label.bee.fertility")),
    BeeTerritory(new TemplateToggleableChromosomes(EnumBeeChromosome.TERRITORY, "bqforestry.label.bee.territory")),
    BeeEffect(new TemplateToggleableChromosomes(EnumBeeChromosome.EFFECT, "bqforestry.label.bee.effect")),
    BeeTemperature(new TemplateToggleableChromosomes(EnumBeeChromosome.TEMPERATURE_TOLERANCE, "bqforestry.label.bee.temp")),
    BeeHumidity(new TemplateToggleableChromosomes(EnumBeeChromosome.HUMIDITY_TOLERANCE, "bqforestry.label.bee.humidity")),
    BeeSleep(new TemplateBoolean(EnumBeeChromosome.NEVER_SLEEPS, "bqforestry.label.bee.sleeps")),
    BeeRain(new TemplateBoolean(EnumBeeChromosome.TOLERATES_RAIN, "bqforestry.label.bee.rain")),
    BeeCave(new TemplateBoolean(EnumBeeChromosome.CAVE_DWELLING, "bqforestry.label.bee.dwelling")),
    BeeFlowerProvider(new TemplateToggleableChromosomes(EnumBeeChromosome.FLOWER_PROVIDER, "bqforestry.label.bee.flowers")),
    BeeGrowth(new PanelBeeGrowth());

    // TODO: Implement changing of the "type" or "growth" of the bees. This was an oversight...
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
