package com.nicjames2378.bqforestry.client.gui.editors.tasks.canvas.panels;

import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.canvas.abstractions.BQScreenCanvas;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.canvas.abstractions.IControlPanel;

@SuppressWarnings("unused") // Stupid IntelliJ being a stupid whiner...
public enum BeePanelControls {
    // new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0)
    None(new ControlEmpty(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0))),
    Trash(new ControlTrash(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0))),
    BeeSpecies(new ControlSpecies(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0))),
    BeeSpeed(new ControlSpeed(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0)));

    private final IControlPanel canvas;

    BeePanelControls(final IControlPanel canvas) {
        this.canvas = canvas;
    }

    public void get(BQScreenCanvas parent, CanvasEmpty container) {
        canvas.initialize(parent, container);
    }
}
