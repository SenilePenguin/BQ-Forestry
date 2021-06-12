package com.nicjames2378.bqforestry.client.gui.editors.controls;

import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.controls.PanelButton;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.utils.QuestTranslation;
import com.nicjames2378.bqforestry.client.themes.ThemeHandler;
import net.minecraft.client.gui.FontRenderer;

public class BQButton {
    // PresetColor.BTN_DISABLED.getColor()
    public static GuiColorStatic GREY = new GuiColorStatic(128, 128, 128, 255);
    public static GuiColorStatic RED = new GuiColorStatic(211, 33, 45, 255);
    public static GuiColorStatic GREEN = new GuiColorStatic(74, 255, 0, 255);

    // Custom ConfirmButton wrapper class allows setting universal options for the control
    public static class ConfirmButton extends PanelButton {
        public ConfirmButton(IGuiRect rect, int id, String txt) {
            super(rect, id, txt);
            setTextHighlight(GREY, GREEN, RED);
        }
    }

    public static class AddButton extends PanelButton {
        addCallBack callBack = () -> {
        };

        public AddButton(IGuiRect rect, String translationKey, FontRenderer fontRenderer, addCallBack onButtonPress) {
            super(rect, -1, "");
            init(translationKey, fontRenderer, onButtonPress);
        }

        public AddButton(IGuiRect rect, int id, String txt, String translationKey, FontRenderer fontRenderer, addCallBack onButtonPress) {
            super(rect, id, txt);
            init(translationKey, fontRenderer, onButtonPress);
        }

        private void init(String translationKey, FontRenderer fontRenderer, addCallBack onButtonPress) {
            callBack = onButtonPress;
            setIcon(ThemeHandler.ICON_ITEM_ADD.getTexture());
            setTooltip(RenderUtils.splitString(QuestTranslation.translate(translationKey), 128, fontRenderer));
        }

        @Override
        public void onButtonClick() {
            callBack.activate();
        }

        public interface addCallBack {
            void activate();
        }
    }
}
