package com.nicjames2378.bqforestry.client.gui.editors.tasks.abstractions;

import betterquesting.api.questing.IQuest;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.client.gui.GuiScreenCanvas;
import com.nicjames2378.bqforestry.client.gui.editors.panels.PanesBee;
import com.nicjames2378.bqforestry.tasks.TaskForestryRetrieval;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import static com.nicjames2378.bqforestry.utils.UtilitiesBee.DEFAULT_SPECIES;
import static com.nicjames2378.bqforestry.utils.UtilitiesBee.getBaseBee;

public class BQScreenCanvas extends GuiScreenCanvas implements ISelections {
    protected IQuest quest;
    protected TaskForestryRetrieval task;
    private int selectedItem = 0;
    private PanesBee selectedOption = PanesBee.None;

    //region Getters and Setters
    public int getSelectedIndex() {
        return selectedItem;
    }

    public void setSelectedIndex(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    public ItemStack getSelectedItem() {
        int size = task.requiredItems.size();

        if (size > 0 && size >= selectedItem) {
            return task.requiredItems.get(selectedItem).getBaseStack();
        } else {
            return getBaseBee(DEFAULT_SPECIES);
        }
    }

    public PanesBee getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(PanesBee control) {
        if (selectedOption != control) {
            this.selectedOption = control;
            refresh();
        }
    }

    public TaskForestryRetrieval getTaskReference() {
        return task;
    }
    //endregion

    public BQScreenCanvas(GuiScreen parent) {
        super(parent);
    }

    public void updateTaskItem(BigItemStack newItem) {
        if (newItem != null) {
            task.requiredItems.set(getSelectedIndex(), newItem);
        } else {
            deleteTaskItem();
        }
        refresh();
    }

    private void deleteTaskItem() {
        if (getSelectedIndex() >= 0) {
            task.requiredItems.remove(task.requiredItems.get(getSelectedIndex()));
            if (task.requiredItems.size() <= getSelectedIndex()) {
                setSelectedIndex(task.requiredItems.size() - 1);
            }
        }
    }

    protected void refresh() {
        initGui();
    }
}
