package com.nicjames2378.bqforestry.client.gui.editors.panels;

import betterquesting.api2.client.gui.panels.CanvasEmpty;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.abstractions.BQScreenCanvas;

public interface IPanel {
    void initialize(BQScreenCanvas gui, CanvasEmpty canvas);
}
