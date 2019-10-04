package com.nicjames2378.bqforestry.utils;

import com.nicjames2378.bqforestry.Main;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.genetics.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;

import java.util.*;

public class UtilitiesBee {
    private static String[] cacheGrowthStages;

    public static final String DEFAULT_SPECIES = "forestry.speciesCommon";

    public enum BeeTypes {
        larvae("larvae"),
        drone("drone"),
        princess("princess"),
        queen("queen");

        private final String text;

        BeeTypes(final String text) {
            this.text = text;
        }

        public String get() {
            return text;
        }
    }

    public static void initialize() {
        boolean debug = ConfigHandler.cfgDoDebugOutputs;

        for (EnumBeeChromosome chromosome : EnumBeeChromosome.values()) {
            Collection<IAllele> alleles = AlleleManager.alleleRegistry.getRegisteredAlleles(chromosome);
            TreeMap<Integer, String> currentMap = new TreeMap<>();

            Iterator iterator = alleles.iterator();
            for (int i = 0; i < alleles.size(); i++) {
                Object next = iterator.next();

                int value = getValue(chromosome.getAlleleClass(), next);
                currentMap.put(value, next.toString());
                if (debug)
                    Main.log.info(String.format("%1$s %2$d / %3$d found: %4$s (%5$s)", chromosome.toString(), i + 1, alleles.size(), next.toString(), value));
            }
        }
    }


    private static int getValue(Class<? extends IAllele> alleleClass, Object next) {
        // Provides an easy way for me to get the values of certain applicable Alleles
        //      and put them in order based on integer values

        if (IAlleleInteger.class.isAssignableFrom(alleleClass)) {
            IAlleleInteger aa = (IAlleleInteger) next;
            return aa.getValue();

        } else if (IAlleleFloat.class.isAssignableFrom(alleleClass)) {
            IAlleleFloat aa = (IAlleleFloat) next;
            return (int) (aa.getValue() * 10);

        } else if (IAlleleBoolean.class.isAssignableFrom(alleleClass)) {
            IAlleleBoolean aa = (IAlleleBoolean) next;
            return aa.getValue() ? 1 : 0;

        } else if (IAlleleTolerance.class.isAssignableFrom(alleleClass)) {
            IAlleleTolerance aa = (IAlleleTolerance) next;
            String[] val = String.valueOf(aa.getValue()).toLowerCase().split("_");

            // Generate custom Integers based on the values (and how I want them ordered)
            if (val[0].contains("down")) {
                return 10 + Integer.parseInt(val[1]);
            } else if (val[0].contains("up")) {
                return 20 + Integer.parseInt(val[1]);
            } else if (val[0].contains("both")) {
                return 30 + Integer.parseInt(val[1]);
            } else {
                return 0;
            }

        } else if (IAlleleArea.class.isAssignableFrom(alleleClass)) {
            IAlleleArea aa = (IAlleleArea) next;
            Vec3i vec = aa.getValue();
            return vec.getX() + vec.getY() + vec.getZ();
            // return String.format("%1$s %2$s %3$s", vec.getX(), vec.getY(), vec.getZ()).replace(" ", "0");
        }

        return 0;
    }

    private static String getGrowthPath(BeeTypes type) {
        return String.format("bee_%1$s_ge", type.get());
    }

    public static String[] getGrowthStages() {
        // Save a tiny bit of time by caching this list since it can't change at runtime
        if (cacheGrowthStages != null) return cacheGrowthStages;

        ArrayList<String> list = new ArrayList<>();
        for (BeeTypes a : BeeTypes.values()) {
            list.add(a.get());
        }

        cacheGrowthStages = list.toArray(new String[0]);
        return cacheGrowthStages;
    }

    public static ItemStack getBaseBee(String species) {
        return getBaseBee(species, BeeTypes.valueOf(ConfigHandler.cfgBeeType), false);
    }

    public static ItemStack getBaseBee(String species, BeeTypes beeType) {
        return getBaseBee(species, beeType, false);
    }

    public static ItemStack getBaseBee(String species, BeeTypes beeType, boolean requireMated) {
        if (species == null) species = DEFAULT_SPECIES; // Not-a-bee check

        ResourceLocation rl = new ResourceLocation("forestry", getGrowthPath(beeType));
        ItemStack bee = new ItemStack(
                Objects.requireNonNull(Item.REGISTRY.getObject(rl))); // Get the Forestry Bee item
        /*
              tag
              |--Genome
                 |--Chromosomes
                    |--#0
                        |--Slot
                        |--UID0
                        |--UID1
              |--Mate
         */
        NBTTagCompound speciesTag = new NBTTagCompound();
        speciesTag.setByte("Slot", (byte) 0);
        speciesTag.setString("UID0", species);
        speciesTag.setString("UID1", species);

        NBTTagList chromosomes = new NBTTagList();
        chromosomes.appendTag(speciesTag);

        NBTTagCompound genome = new NBTTagCompound();
        genome.setTag("Chromosomes", chromosomes);

        NBTTagCompound itemData = new NBTTagCompound();
        itemData.setTag("Genome", genome);

        if (requireMated) {
            NBTTagCompound mate = new NBTTagCompound();
            itemData.setTag("Mate", mate);
        }

        bee.setTagCompound(itemData);   // Finally add it to the defaultBee
        return bee;
    }

    // Returns null if a properly formatted species tag cannot be found!
    public static String getTrait(ItemStack bee, EnumBeeChromosome trait) {
        if (bee == null) return null;

        if (!bee.hasTagCompound()) return null;
        NBTTagCompound tag = bee.getTagCompound();

        if (!tag.hasKey("Genome")) return null;
        NBTTagCompound genome = tag.getCompoundTag("Genome");

        if (genome.hasNoTags()) return null;
        NBTTagList chromosomes = genome.getTagList("Chromosomes", 10);

        if (chromosomes.hasNoTags()) return null;
        NBTTagCompound nbtTrait = chromosomes.getCompoundTagAt(getIndexFromChromosome(trait));

        return nbtTrait.getString("UID0");
    }

    private static int getIndexFromChromosome(EnumBeeChromosome chromosome) {
        switch (chromosome) {
            case SPECIES:
                return 0;
            case SPEED:
                return 1;
            case LIFESPAN:
                return 2;
            case FERTILITY:
                return 3;
            case TEMPERATURE_TOLERANCE:
                return 4;
            case NEVER_SLEEPS:
                return 5;
            case HUMIDITY_TOLERANCE:
                return 6;
            case TOLERATES_RAIN:
                return 7;
            case CAVE_DWELLING:
                return 8;
            case FLOWER_PROVIDER:
                return 9;
            case FLOWERING:
                return 10;
            case TERRITORY:
                return 11;
            case EFFECT:
                return 12;
            default:
                return -1;
        }
    }

    public static BeeTypes getGrowthLevel(ItemStack bee) {
        for (String s : getGrowthStages()) {
            if (bee.getUnlocalizedName().contains(s)) return BeeTypes.valueOf(s);
        }

        return BeeTypes.valueOf(ConfigHandler.cfgBeeType);
    }

    public static boolean hasValidgrowthLevel(ItemStack bee) {
        for (String s : getGrowthStages()) {
            if (bee.getUnlocalizedName().contains(s)) return true;
        }
        return false;
    }

    public static boolean isMated(ItemStack item) {
        return item.hasTagCompound() && item.getTagCompound().hasKey("Mate");
        //return item.getTagCompound() != null && item.getTagCompound().hasKey("Mate");
    }

    public static boolean checkTraitsMatch(ItemStack beeA, ItemStack beeB, EnumBeeChromosome trait) {
        if (getTrait(beeA, trait) == null || getTrait(beeB, trait) == null)
            return false;                                                   // If one of the bees are missing the properly formatted species tag, they don't match
        return Objects.equals(getTrait(beeA, trait), getTrait(beeB, trait));    // Return whether they match or not
    }

    public static void listAllSpecies() {
        Main.log.info("Config ListAllBees is TRUE. Outputting bees list now.");
        Main.log.info("===========================================================");

        Collection<IAllele> species = AlleleManager.alleleRegistry.getRegisteredAlleles(EnumBeeChromosome.SPECIES);
        Iterator a = species.iterator();
        for (int i = 0; i < species.size(); i++) {
            String spe = a.next().toString();
            Main.log.info(String.format("Bees species found: %1$d / %2$d - %3$s", i + 1, species.size(), spe));
        }

        Main.log.info("===========================================================");
    }
}
