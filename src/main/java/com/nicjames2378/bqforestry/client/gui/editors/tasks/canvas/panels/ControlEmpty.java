package com.nicjames2378.bqforestry.client.gui.editors.tasks.canvas.panels;

import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasEmpty;

public class ControlEmpty extends CanvasEmpty implements IControlPanel {

    public ControlEmpty(IGuiRect rect) {
        super(rect);
    }

    @Override
    public IControlPanel initialize() {
        return this;
    }
}
