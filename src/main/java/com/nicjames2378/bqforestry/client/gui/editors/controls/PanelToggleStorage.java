package com.nicjames2378.bqforestry.client.gui.editors.controls;

import betterquesting.api.misc.ICallback;
import betterquesting.api2.client.gui.controls.PanelButtonStorage;
import betterquesting.api2.client.gui.misc.IGuiRect;
import net.minecraft.util.text.TextFormatting;

@SuppressWarnings("unchecked")
public class PanelToggleStorage<T> extends PanelButtonStorage {
    private boolean isToggled = false;
    private String text;

    public PanelToggleStorage(IGuiRect rect, int id, String txt, Object value) {
        super(rect, id, txt, value);
        this.text = txt;
    }

    @Override
    public void onButtonClick() {
        super.onButtonClick();
        setToggledStatus(isToggled ^= true);
    }

    //@SuppressWarnings("unchecked")
    public PanelToggleStorage setCallback(ICallback callback) {
        super.setCallback(callback);
        return this;
    }

    //@SuppressWarnings("unchecked")
    public PanelToggleStorage setStoredValue(Object value) {
        super.setStoredValue(value);
        return this;
    }

    public boolean getToggledStatus() {
        return this.isToggled;
    }

    public void setToggledStatus(boolean value) {
        this.isToggled = value;
        setText((isToggled ? TextFormatting.GREEN : "").toString() + this.text);
    }
}
