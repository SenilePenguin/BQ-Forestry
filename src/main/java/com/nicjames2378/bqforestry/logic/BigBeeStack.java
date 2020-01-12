package com.nicjames2378.bqforestry.logic;

import betterquesting.api.utils.BigItemStack;
import net.minecraft.item.ItemStack;

import static com.nicjames2378.bqforestry.utils.UtilitiesBee.*;

public class BigBeeStack extends BigItemStack {

    public boolean requireMated = false;
    public BeeTypes beeType;

    public BigBeeStack(ItemStack stack) {
        super(stack);
        readFromNBT();
    }

    private void readFromNBT() {
        requireMated = isMated(getBaseStack());
        beeType = getGrowthLevel(getBaseStack());
    }
}
