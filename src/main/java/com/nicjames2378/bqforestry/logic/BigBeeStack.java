package com.nicjames2378.bqforestry.logic;

import betterquesting.api.utils.BigItemStack;
import com.nicjames2378.bqforestry.BQ_Forestry;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import forestry.api.apiculture.EnumBeeChromosome;
import net.minecraft.item.ItemStack;

import static com.nicjames2378.bqforestry.utils.UtilitiesBee.*;

public class BigBeeStack extends BigItemStack {

    public boolean requireMated = false;
    private BeeTypes beeType = BeeTypes.valueOf(ConfigHandler.cfgBeeType);

    public BigBeeStack setType(BeeTypes type) {
//        return new BigBeeStack(getBaseBee(UtilitiesBee.DEFAULT_SPECIES, UtilitiesBee.BeeTypes.valueOf(ConfigHandler.cfgBeeType), ConfigHandler.cfgOnlyMated));

        if (type != beeType) {
            // Generate new BaseBee of type
            // Copy NBT to it
            // ???
            // Profit?

            ItemStack bee = getBaseStack();

            String species = getTrait(bee, EnumBeeChromosome.SPECIES, true)[0];
            ItemStack sNew = getBaseBee(species, type, requireMated);

            if (bee.hasTagCompound()) {
                sNew.setTagCompound(bee.getTagCompound());
            }
            BigBeeStack newBee = new BigBeeStack(sNew);

            beeType = type;

            BQ_Forestry.debug(String.format("TYPE: current=%1$s, new=%2$s", beeType, newBee.getBeeType()));
            return newBee;
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

    public BigBeeStack(BigItemStack stack) {
        super(stack.getBaseStack());
        readFromNBT();
    }

    private void readFromNBT() {
        requireMated = isMated(getBaseStack());
        beeType = getGrowthLevel(getBaseStack());
    }
}
