package com.nicjames2378.bqforestry.utils;

import com.nicjames2378.bqforestry.Main;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class UtilitiesBee {
    private static String[] cacheTypes;

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

    private static String getTypePath(BeeTypes type) {
/*        switch (type) {
            case larvae:
                return pathLarvae;
            case drone:
                return pathDrone;
            case princess:
                return pathPrincess;
            case queen:
                return pathQueen;
            default:
                return null;
        }*/

        return String.format("bee_%1$s_ge", type.get());
    }

    public static String[] getAllTypes() {
        // Save a tiny bit of time by caching this list since it can't change at runtime
        if (cacheTypes != null) return cacheTypes;

        ArrayList<String> list = new ArrayList<>();
        for (BeeTypes a : BeeTypes.values()) {
            list.add(a.get());
        }

        cacheTypes = list.toArray(new String[0]);
        return cacheTypes;
    }

    public static ItemStack getBaseBee(String species) {
        return getBaseBee(species, BeeTypes.valueOf(ConfigHandler.cfgBeeType), false);
    }

    public static ItemStack getBaseBee(String species, BeeTypes beeType) {
        return getBaseBee(species, beeType, false);
    }

    public static ItemStack getBaseBee(String species, BeeTypes beeType, boolean requireMated) {
        if (species == null) species = DEFAULT_SPECIES; // Not-a-bee check

        ResourceLocation rl = new ResourceLocation("forestry", getTypePath(beeType));
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
    public static String getSpecies(ItemStack bee) {
        if (bee == null) return null;

        if (!bee.hasTagCompound()) return null;
        NBTTagCompound tag = bee.getTagCompound();

        if (!tag.hasKey("Genome")) return null;
        NBTTagCompound genome = tag.getCompoundTag("Genome");

        if (genome.hasNoTags()) return null;
        NBTTagList chromosomes = genome.getTagList("Chromosomes", 10);

        if (chromosomes.hasNoTags()) return null;
        NBTTagCompound species = chromosomes.getCompoundTagAt(0);

        return species.getString("UID0");
    }

    public static BeeTypes getType(ItemStack bee) {
        for (String s : getAllTypes()) {
            if (bee.getUnlocalizedName().contains(s)) return BeeTypes.valueOf(s);
        }

        return BeeTypes.valueOf(ConfigHandler.cfgBeeType);
    }

    public static boolean isMated(ItemStack item) {
        return item.hasTagCompound() && item.getTagCompound().hasKey("Mate");
        //return item.getTagCompound() != null && item.getTagCompound().hasKey("Mate");
    }

    public static boolean checkMatchSpecies(ItemStack beeA, ItemStack beeB) {
        if (getSpecies(beeA) == null || getSpecies(beeB) == null)
            return false;                                                   // If one of the bees are missing the properly formatted species tag, they don't match
        return Objects.equals(getSpecies(beeA), getSpecies(beeB));    // Return whether they match or not
    }

    public static void listAllSpecies() {
        Main.log.info("Config ListAllBees is TRUE. Outputting bees list now.");
        Main.log.info("===========================================================");

        Collection<IAllele> species = AlleleManager.alleleRegistry.getRegisteredAlleles(EnumBeeChromosome.SPECIES);
        Iterator a = species.iterator();
        for (int i = 0; i < species.size(); i++) {
            String spe = a.next().toString();
            // Handle Fixing the Forestry bug
            if (ConfigHandler.cfgEderEndedFix && spe.equalsIgnoreCase("ender"))
                spe = spe.replace("ender", "ended") + "(FixEnderEnded Enabled!)";
            Main.log.info(String.format("Bees species found: %1$d / %2$d - %3$s", i + 1, species.size(), spe));
        }

        Main.log.info("===========================================================");
    }
}
