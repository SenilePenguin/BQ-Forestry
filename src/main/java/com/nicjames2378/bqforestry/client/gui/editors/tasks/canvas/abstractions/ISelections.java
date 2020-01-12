package com.nicjames2378.bqforestry.client.gui.editors.tasks.canvas.abstractions;

import com.nicjames2378.bqforestry.client.gui.editors.tasks.canvas.panels.BeePanelControls;

public interface ISelections {
    int selectedItem = 0;
    String selectedSpecies = null;
    String selectedType = null;
    BeePanelControls selectedOption = BeePanelControls.None;
}
