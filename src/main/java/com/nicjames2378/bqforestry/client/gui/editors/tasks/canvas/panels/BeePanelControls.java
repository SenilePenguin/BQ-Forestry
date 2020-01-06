package com.nicjames2378.bqforestry.client.gui.editors.tasks.canvas.panels;

import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiTransform;

@SuppressWarnings("unused") // Stupid IntelliJ being a stupid whiner...
public enum BeePanelControls {
    // new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0)
    None(new ControlEmpty(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0))),
    BeeSpecies(new ControlSpecies(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0)));

    private final IControlPanel canvas;

    BeePanelControls(final IControlPanel canvas) {
        this.canvas = canvas;
    }

    public IControlPanel get() {
        return canvas.initialize();
    }
}
