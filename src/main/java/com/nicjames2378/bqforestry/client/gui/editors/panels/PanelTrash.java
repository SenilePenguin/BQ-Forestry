package com.nicjames2378.bqforestry.client.gui.editors.panels;

import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.utils.QuestTranslation;
import com.nicjames2378.bqforestry.client.gui.editors.panels.templates.TemplateEmpty;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.abstractions.BQScreenCanvas;

import static com.nicjames2378.bqforestry.client.gui.editors.controls.BQButton.*;

public class PanelTrash extends TemplateEmpty {

    public PanelTrash(IGuiRect rect) {
        super(rect);
    }

    @Override
    public void initialize(BQScreenCanvas gui, CanvasEmpty canvas) {
        // Disclaimer message
        PanelTextBox txtDisclaimer = new PanelTextBox(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(4, 24, 4, 0), 0), QuestTranslation.translate("bqforestry.panel.information.delete", gui.getSelectedIndex()));
        txtDisclaimer.setFontSize(22).enableShadow(true);
        canvas.addPanel(txtDisclaimer);

        // Confirm Button
        ConfirmButton btnConfirm = new ConfirmButton(new GuiTransform(GuiAlign.BOTTOM_EDGE, new GuiPadding(4, -28, 4, 4), 0), -1, QuestTranslation.translate("bqforestry.btn.confirm")) {
            @Override
            public void onButtonClick() {
                // Passing a null will delete the task item
                gui.updateTaskItem(null);
            }
        };

        // Change colors for Trash button to make it stand out
        btnConfirm.setTextHighlight(GREY, RED, GREEN);
        canvas.addPanel(btnConfirm);
    }
}
