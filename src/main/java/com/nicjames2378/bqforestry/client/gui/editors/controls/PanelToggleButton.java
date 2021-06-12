package com.nicjames2378.bqforestry.client.gui.editors.controls;

import betterquesting.api2.client.gui.controls.PanelButton;
import betterquesting.api2.client.gui.misc.IGuiRect;

public class PanelToggleButton extends PanelButton {
    private State state = State.EITHER;

    public PanelToggleButton(IGuiRect rect, int id, String txt) {
        super(rect, id, txt);
    }

    @Override
    public void onButtonClick() {
        super.onButtonClick();
        setToggledStatus(state.next());
    }

    public State getToggledStatus() {
        return this.state;
    }

    public void setToggledStatus(State value) {
        this.state = value;
    }

    public State incrementToggledStatus() {
        return state = state.next();
    }

    public enum State {
        ON("forestry.boolTrue"),
        OFF("forestry.boolFalse"),
        EITHER("");

        private static State[] vals = values();
        private final String nbtText;

        State(String nbtText) {
            this.nbtText = nbtText;
        }

        public String get() {
            return nbtText;
        }

        public State next() {
            if (this.ordinal() == State.values().length - 1) {
                return vals[0];
            } else {
                return vals[(this.ordinal() + 1) % vals.length];
            }
        }
    }
}
