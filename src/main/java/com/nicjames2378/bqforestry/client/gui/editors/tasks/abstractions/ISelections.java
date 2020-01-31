package com.nicjames2378.bqforestry.client.gui.editors.tasks.abstractions;

import com.nicjames2378.bqforestry.client.gui.editors.panels.PanesBee;

public interface ISelections {
    int selectedItem = 0;
    String selectedSpecies = null;
    String selectedType = null;
    PanesBee selectedOption = PanesBee.None;
}
