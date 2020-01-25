package com.nicjames2378.bqforestry.client.gui.editors.tasks.canvas.abstractions;

import betterquesting.api2.client.gui.controls.PanelButton;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;

public interface IControlPanel {
    void initialize(BQScreenCanvas gui, CanvasEmpty canvas);

    // Custom ConfirmButton wrapper class allows setting universal options for the control
    class ConfirmButton extends PanelButton {
        // PresetColor.BTN_DISABLED.getColor()
        public static final GuiColorStatic GREY = new GuiColorStatic(128, 128, 128, 255);
        public static final GuiColorStatic RED = new GuiColorStatic(211, 33, 45, 255);
        public static final GuiColorStatic GREEN = new GuiColorStatic(74, 255, 0, 255);

        public ConfirmButton(IGuiRect rect, int id, String txt) {
            super(rect, id, txt);
            setTextHighlight(GREY, GREEN, RED);
        }
    }
}
