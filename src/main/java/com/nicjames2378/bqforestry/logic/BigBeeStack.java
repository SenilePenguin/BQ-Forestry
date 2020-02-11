package com.nicjames2378.bqforestry.logic;

import betterquesting.api.utils.BigItemStack;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import forestry.api.apiculture.EnumBeeChromosome;
import net.minecraft.item.ItemStack;

import static com.nicjames2378.bqforestry.utils.UtilitiesBee.*;

public class BigBeeStack extends BigItemStack {

    public boolean requireMated = false;
    private BeeTypes beeType = BeeTypes.valueOf(ConfigHandler.cfgBeeType);

    public BigBeeStack setType(BeeTypes type) {
        if (type != beeType) {
            // Generate new BaseBee of type
            // Copy NBT to it
            // ???
            // Profit?

            ItemStack bee = getBaseStack();
            BigBeeStack newBee = new BigBeeStack(getBaseBee(getTrait(bee, EnumBeeChromosome.SPECIES, true)[0], type, requireMated));

            if (bee.hasTagCompound()) {
                newBee.writeToNBT(bee.getTagCompound());
            }
            beeType = type;
        }
        return this;
    }

    public BeeTypes getBeeType() {
        return beeType;
    }

    public BigBeeStack(ItemStack stack) {
        super(stack);
        readFromNBT();
    }

    private void readFromNBT() {
        requireMated = isMated(getBaseStack());
        beeType = getGrowthLevel(getBaseStack());
    }
}
