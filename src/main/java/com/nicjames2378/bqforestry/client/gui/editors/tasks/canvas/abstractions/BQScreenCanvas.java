package com.nicjames2378.bqforestry.client.gui.editors.tasks.canvas.abstractions;

import betterquesting.api.questing.IQuest;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.client.gui.GuiScreenCanvas;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import com.nicjames2378.bqforestry.tasks.TaskForestryRetrieval;
import com.nicjames2378.bqforestry.utils.UtilitiesBee;
import forestry.api.apiculture.EnumBeeChromosome;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import static com.nicjames2378.bqforestry.utils.UtilitiesBee.DEFAULT_SPECIES;
import static com.nicjames2378.bqforestry.utils.UtilitiesBee.getBaseBee;

public class BQScreenCanvas extends GuiScreenCanvas implements ISelections {
    protected IQuest quest;
    protected TaskForestryRetrieval task;

    private int selectedItem = 0;
//    private String selectedSpecies;
//    private String selectedType;

    //region Getters and Setters
    public int getSelectedIndex() {
        return selectedItem;
    }

    public void setSelectedIndex(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getSelectedSpecies() {
        int size = task.requiredItems.size();

        if (size > 0 && size <= selectedItem) {
            return UtilitiesBee.getTrait(task.requiredItems.get(selectedItem).getBaseStack(), EnumBeeChromosome.SPECIES, true)[0];
        } else {
            return DEFAULT_SPECIES;
        }

//        if (selectedSpecies != null) {
//            return selectedSpecies;
//        } else {
//            return DEFAULT_SPECIES;
//        }
    }

    public void setSelectedSpecies(String selectedSpecies) {
        /*if (selectedSpecies != null) {
            task.requiredItems.get(selectedItem).getBaseStack();
        } else {
            task.requiredItems.get(selectedItem).
        }*/

//        if (selectedSpecies == null) {
//            this.selectedSpecies = DEFAULT_SPECIES;
//        } else {
//            this.selectedSpecies = selectedSpecies;
//        }
    }

    public String getSelectedType() {
        int size = task.requiredItems.size();

        if (size > 0 && size >= selectedItem) {
            return UtilitiesBee.getGrowthLevel(task.requiredItems.get(selectedItem).getBaseStack()).toString();
        } else {
            return ConfigHandler.cfgBeeType;
        }

//        if (selectedType != null) {
//            return selectedType;
//        } else {
//            return ConfigHandler.cfgBeeType;
//        }
    }

    public void setSelectedType(String selectedType) {
//        if (selectedType == null) {
//            this.selectedType = ConfigHandler.cfgBeeType;
//        } else {
//            this.selectedType = selectedType;
//        }
    }

    public ItemStack getSelectedItem() {
        int size = task.requiredItems.size();

        if (size > 0 && size >= selectedItem) {
            return task.requiredItems.get(selectedItem).getBaseStack();
        } else {
            return getBaseBee(DEFAULT_SPECIES);
        }
    }
    //endregion

    public BQScreenCanvas(GuiScreen parent) {
        super(parent);
    }

    public void updateTaskItem(BigItemStack newItem) {
        task.requiredItems.set(getSelectedIndex(), newItem);
        refresh();
    }

    protected void refresh() {
        initGui();
    }
}
