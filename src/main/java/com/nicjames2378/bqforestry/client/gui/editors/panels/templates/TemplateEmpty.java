package com.nicjames2378.bqforestry.client.gui.editors.panels.templates;

import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import com.nicjames2378.bqforestry.client.gui.editors.panels.IPanel;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.abstractions.BQScreenCanvas;

public class TemplateEmpty extends CanvasEmpty implements IPanel {

    public TemplateEmpty(IGuiRect rect) {
        super(rect);
    }

    @Override
    public void initialize(BQScreenCanvas gui, CanvasEmpty canvas) {
    }
}
