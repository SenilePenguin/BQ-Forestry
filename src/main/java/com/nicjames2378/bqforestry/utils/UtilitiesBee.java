package com.nicjames2378.bqforestry.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class UtilitiesBee {
    private static String princessPath = "bee_princess_ge";

    public static ItemStack getBaseBee(String species) {
        ResourceLocation rl = new ResourceLocation("forestry", princessPath);
        ItemStack bee = new ItemStack(
                Objects.requireNonNull(Item.REGISTRY.getObject(rl))); // Get the Forestry Princess item

        NBTTagCompound speciesTag = new NBTTagCompound();   // Create the species compound
        speciesTag.setString("UID0", species);              // and make it Common
        speciesTag.setString("UID1", species);              // second one makes it purebred in case anyone wants to compare against that
        speciesTag.setByte("Slot", (byte) 0);               // Needed to have a properly formatted bee

        NBTTagList chromosomes = new NBTTagList();          // Create and add the species genes to the chromosome
        chromosomes.appendTag(speciesTag);

        NBTTagCompound genome = new NBTTagCompound();       //Create and add the chromosome to the genome
        genome.setTag("Chromosomes", chromosomes);

        NBTTagCompound itemData = new NBTTagCompound();     // Another encapsulation. Forestry is very organized.
        itemData.setTag("Genome", genome);

        bee.setTagCompound(itemData);   // Finally add it to the defaultBee
        return bee;
    }

    // Returns null if a properly formatted species tag cannot be found!
    public static String getBeeSpecies(ItemStack bee) {
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

    public static boolean checkBeesMatchSpecies(ItemStack beeA, ItemStack beeB) {
        if (getBeeSpecies(beeA) == null || getBeeSpecies(beeB) == null)
            return false;                                                   // If one of the bees are missing the properly formatted species tag, they don't match
        return Objects.equals(getBeeSpecies(beeA), getBeeSpecies(beeB));    // Return whether they match or not
    }
}
