package com.nicjames2378.bqforestry.utils;

import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.utils.QuestTranslation;
import com.nicjames2378.bqforestry.BQ_Forestry;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.genetics.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextFormatting;

import java.util.*;

import static com.nicjames2378.bqforestry.utils.StringUtils.flattenArray;
import static com.nicjames2378.bqforestry.utils.StringUtils.indexOfFirstCapital;

public class UtilitiesBee {
    private static String[] cacheGrowthStages;

    public static final String DEFAULT_SPECIES = "forestry.speciesCommon";
    public static final String _INVALID_SPECIES_STRING = "(MISSING) ";

    @SuppressWarnings("unused") // Stupid IntelliJ being a stupid whiner...
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
        for (EnumBeeChromosome chromosome : EnumBeeChromosome.values()) {
            Collection<IAllele> alleles = AlleleManager.alleleRegistry.getRegisteredAlleles(chromosome);

            Iterator iterator = alleles.iterator();
            for (int i = 0; i < alleles.size(); i++) {
                Object next = iterator.next();

                int value = getValue(chromosome.getAlleleClass(), next);
                BQ_Forestry.debug(String.format("%1$s %2$d / %3$d found: %4$s (%5$s)", chromosome.toString(), i + 1, alleles.size(), next.toString(), value));
            }
        }
    }

    public static String getDisplayName(ItemStack stack) {
        // getDisplayName() can create a NullPointerException down the stack if the item has an improper Species tag. Nothing I can do except
        //      check that bee's species is actually in the database before running a getDisplayName() on it

        String speciesTrait = getTrait(stack, EnumBeeChromosome.SPECIES, true)[0];
        return checkTraitIsInDatabase(EnumBeeChromosome.SPECIES, speciesTrait) ? stack.getDisplayName() : TextFormatting.RED + _INVALID_SPECIES_STRING + speciesTrait;
    }

    public static TreeMap<Integer, String> getAllelesForChromosome(EnumBeeChromosome chromosome) {
        Collection<IAllele> alleles = AlleleManager.alleleRegistry.getRegisteredAlleles(chromosome);
        TreeMap<Integer, String> orderedMap = new TreeMap<>();

        Iterator iterator = alleles.iterator();
        short counter = 0;
        for (int i = 0; i < alleles.size(); i++) {
            Object next = iterator.next();

            int value = getValue(chromosome.getAlleleClass(), next);
            if (value < 0) counter++;
            orderedMap.put((value >= 0 ? value : i), next.toString());
        }

        // Special handling for String-based values that cannot be easily sorted in getValue.
        // This will just alphabetize them for the sake of cleanliness.
        if (counter >= 2) {
            TreeMap<Integer, String> newOrder = new TreeMap<>();
            SortedSet<String> set = new TreeSet<>(orderedMap.values());
            Iterator iter = set.iterator();
            for (int i = 0; i < set.size(); i++) {
                newOrder.put(i, (String) iter.next());
            }
            return newOrder;
        }

        // If we didn't have to do special handling, return the original map.
        return orderedMap;
    }

    private static int getValue(Class<? extends IAllele> alleleClass, Object item) {
        // Provides an easy way for me to get the values of certain applicable Alleles
        //      and put them in order based on integer values

        // Integers return themselves
        if (IAlleleInteger.class.isAssignableFrom(alleleClass)) {
            IAlleleInteger aa = (IAlleleInteger) item;
            return aa.getValue();

            // Floats convert to integers
        } else if (IAlleleFloat.class.isAssignableFrom(alleleClass)) {
            IAlleleFloat aa = (IAlleleFloat) item;
            return (int) (aa.getValue() * 10);

            // Booleans return 1 or 0
        } else if (IAlleleBoolean.class.isAssignableFrom(alleleClass)) {
            IAlleleBoolean aa = (IAlleleBoolean) item;
            return aa.getValue() ? 1 : 0;

            // Tolerances have special rules to convert to integers
        } else if (IAlleleTolerance.class.isAssignableFrom(alleleClass)) {
            IAlleleTolerance aa = (IAlleleTolerance) item;
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

            // Areas return an integer equal to the area rather than the proportions
        } else if (IAlleleArea.class.isAssignableFrom(alleleClass)) {
            IAlleleArea aa = (IAlleleArea) item;
            Vec3i vec = aa.getValue();
            return vec.getX() + vec.getY() + vec.getZ();
            // return String.format("%1$s %2$s %3$s", vec.getX(), vec.getY(), vec.getZ()).replace(" ", "0");
        }

        // Traits without handling return a -1
        return -1;
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
                    |--#0 (Species)
                        |--Slot
                        |--UID0
                        |--UID1
              |--Mate
              |--ChromosomesList
                 |--speed
                 |--lifespan
         */
        NBTTagCompound speciesTag = new NBTTagCompound();
        speciesTag.setByte("Slot", (byte) 0);
        speciesTag.setString("UID0", species);
        speciesTag.setString("UID1", species);

        // NBTTagList speciesTag = new NBTTagList();
        // speciesTag.appendTag(new NBTTagString(species));

        NBTTagList chromosomes = new NBTTagList();
        chromosomes.appendTag(speciesTag);

        NBTTagCompound genome = new NBTTagCompound();
        genome.setTag("Chromosomes", chromosomes);

        NBTTagCompound itemData = new NBTTagCompound();
        itemData.setTag("Genome", genome);

        setMated(bee, requireMated);

        bee.setTagCompound(itemData);   // Finally add it to the defaultBee
        return bee;
    }

    // Gets a duplicate of a BigItemStack with it's it values possibly replaced to make Forestry not throw a fit.
    public static BigItemStack getSafeStack(BigItemStack stack) {
        BigItemStack safeStack = stack.copy();
        String stackDName = getDisplayName(safeStack.getBaseStack());
//        BQ_Forestry.debug("[getSafeStack] stackDName: " + stackDName);
        // If we get the displayName and it contains the error string,
        if (stackDName.contains(_INVALID_SPECIES_STRING)) {
//            BQ_Forestry.debug("[getSafeStack] ERROR!!");
            // Replace the NBT with something Forestry won't whine about
            safeStack.getBaseStack().setTagCompound(getBaseBee(DEFAULT_SPECIES).getTagCompound());
            // Then overwrite it's display name so people still know what they're looking at
            safeStack.getBaseStack().setStackDisplayName(stackDName);
        }


        return safeStack;
    }

    public static void setMated(ItemStack bee, boolean isMated) {
        if (bee == null) throw new NullPointerException();

        NBTTagCompound tag = bee.getTagCompound();
        if (tag == null) tag = new NBTTagCompound();

        if (isMated) tag.setTag("Mate", new NBTTagCompound());
        else if (tag.hasKey("Mate")) tag.removeTag("Mate");
    }

    public static HashMap<EnumBeeChromosome, HashSet<String>> getAllTraits(ItemStack bee, boolean forceForestryMethod) {
        HashMap<EnumBeeChromosome, HashSet<String>> retMap = new HashMap<>();

        for (EnumBeeChromosome chromosome : EnumBeeChromosome.values()) {
            // Not sure which is faster and more memory efficient. Needs testing
            //      (but top is assumed more efficient as it's not making a throw-away list)
            HashSet<String> set = new HashSet<>();
            String[] array = getTrait(bee, chromosome, forceForestryMethod);

            // Skip adding this chromosome to the map if there aren't any traits
            if (array.length <= 0 || array[0].equals("")) continue;
            Collections.addAll(set, array);
            //HashSet<String> set = new HashSet<>(Arrays.asList(getTrait(bee, chromosome)));

            retMap.put(chromosome, set);
        }

        return retMap;
    }

    /*public static String[] getTrait(ItemStack bee, EnumBeeChromosome chromosome) {
        return getTrait(bee, chromosome, false);
    }*/

    public static String[] getTrait(ItemStack bee, EnumBeeChromosome chromosome, boolean forceForestryMethod) {
        String[] _EMPTY = new String[]{""};

        if (bee == null) return _EMPTY;

        if (!bee.hasTagCompound()) return _EMPTY;
        NBTTagCompound tag = bee.getTagCompound();

        if (!tag.hasKey("Genome")) return _EMPTY;
        NBTTagCompound tagGenome = tag.getCompoundTag("Genome");

        if (tagGenome.hasNoTags()) return _EMPTY;
        String[] ret;

        // Due to constraints within Forestry that I have no ability to fix, I have to do some special conditioning
        //      for the Species trait. This prevents me from having it use a list, as well as causing plenty of
        //      other headaches. >:(
        // Species has to be a TAG_Compound, while we want everything else to be saved under a TAG_List
        // Also, we get the trait under "UID0" if asked to use the Forestry Method
        if (chromosome == EnumBeeChromosome.SPECIES || forceForestryMethod) {
            NBTTagList tagStupidSpeciesIsStupid = tagGenome.getTagList("Chromosomes", 10); // 10 = TAG_Compound
            if (tagStupidSpeciesIsStupid.hasNoTags())
                return _EMPTY; // The TagList is a assigned a new NBTTagList if it doesn't exist

            NBTTagCompound tagTrait = (NBTTagCompound) tagStupidSpeciesIsStupid.get(getIndexFromChromosome(chromosome));

            ret = new String[]{tagTrait.getString("UID0")};

        } else {
            // These are assigned new ones if they can't be found in the "get"
            NBTTagCompound tagChromosomesList = tagGenome.getCompoundTag("ChromosomesList");
            NBTTagList nbtTraitList = tagChromosomesList.getTagList(chromosome.getName(), 8); //8=TAG_String

            ArrayList<String> returnValue = new ArrayList<>();

            for (int i = 0; i < nbtTraitList.tagCount(); i++) {
                returnValue.add(nbtTraitList.getStringTagAt(i));
            }

            if (returnValue.size() <= 0) ret = _EMPTY;
            else ret = returnValue.toArray(new String[0]);
        }
        return ret;
    }

    public static void clearTraits(ItemStack bee, EnumBeeChromosome chromosome) {
        // This shouldn't have happened....
        if (bee == null) throw new NullPointerException();

        NBTTagCompound tag = bee.getTagCompound();
        if (tag == null) tag = new NBTTagCompound();

        NBTTagCompound tagGenome = tag.getCompoundTag("Genome");

        // forestry will crash if we clear the species trait, so we'll default it instead
        if (chromosome == EnumBeeChromosome.SPECIES) {
            writeTrait(bee, chromosome, DEFAULT_SPECIES);
        } else {
            // Can this line be removed? getCompoundTag should return a new one if it's not there, right?
            if (!tagGenome.hasKey("ChromosomesList")) tagGenome.setTag("ChromosomesList", new NBTTagCompound());

            NBTTagCompound tagChromosomesList = tagGenome.getCompoundTag("ChromosomesList");
            // The TagCompound is a assigned a new NBTTagList if it doesn't exist

            if (!tagChromosomesList.hasKey(chromosome.getName())) {
                tagChromosomesList.setTag(chromosome.getName(), new NBTTagList());
            }
            NBTTagList nbtTraitList = tagChromosomesList.getTagList(chromosome.getName(), 8); //8=TAG_String

            // Have to use a temporary variable for storing the count or else we get an issue where only half are deleted each time
            int a = nbtTraitList.tagCount();
            for (int i = 0; i < a; i++) {
                nbtTraitList.removeTag(0);
            }
        }
    }

    public static void writeTrait(ItemStack bee, EnumBeeChromosome chromosome, String trait) {
        // This shouldn't have happened....
        if (bee == null) throw new NullPointerException();

        NBTTagCompound tag = bee.getTagCompound();
        if (tag == null) tag = new NBTTagCompound();

        NBTTagCompound tagGenome = tag.getCompoundTag("Genome");

        // Due to constraints within Forestry that I have no ability to fix, I have to do some special conditioning
        //      for the Species trait. This prevents me from having it use a list, as well as causing plenty of
        //      other headaches. >:(
        // Species has to be a TAG_Compound, while we want everything else to be saved under a TAG_List

        if (chromosome == EnumBeeChromosome.SPECIES) {
            NBTTagList tagStupidSpeciesIsStupid = tagGenome.getTagList("Chromosomes", 10);
            // The TagList is a assigned a new NBTTagList if it doesn't exist

            NBTTagCompound tagTrait = (NBTTagCompound) tagStupidSpeciesIsStupid.get(0); //getIndexFromChromosome(chromosome));
            tagTrait.setString("UID0", trait);
            tagTrait.setString("UID1", trait);

        } else {
            // Can this line be removed? getCompoundTag should return a new one if it's not there, right?
            if (!tagGenome.hasKey("ChromosomesList")) tagGenome.setTag("ChromosomesList", new NBTTagCompound());

            NBTTagCompound tagChromosomesList = tagGenome.getCompoundTag("ChromosomesList");
            // The TagCompound is a assigned a new NBTTagList if it doesn't exist

            if (!tagChromosomesList.hasKey(chromosome.getName())) {
                tagChromosomesList.setTag(chromosome.getName(), new NBTTagList());
            }
            NBTTagList nbtTraitList = tagChromosomesList.getTagList(chromosome.getName(), 8); //8=TAG_String

            NBTTagString str = new NBTTagString(trait);
            boolean doWrite = true;
            for (int i = 0; i < nbtTraitList.tagCount(); i++) {
                if (nbtTraitList.get(i).equals(str)) {
                    doWrite = false;
                }
            }
            if (doWrite) nbtTraitList.appendTag(new NBTTagString(trait));
        }
    }

    // Used to map Forestry chromosomes to their NBT indices
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

    public static boolean hasValidGrowthLevel(ItemStack bee) {
        for (String s : getGrowthStages()) {
            if (bee.getUnlocalizedName().contains(s)) return true;
        }
        return false;
    }

    public static boolean isMated(ItemStack item) {
        return item.hasTagCompound() && item.getTagCompound().hasKey("Mate");
        //return item.getTagCompound() != null && item.getTagCompound().hasKey("Mate");
    }

    public static boolean checkTraitsMatch(ItemStack rStack, ItemStack invStack, EnumBeeChromosome trait) {
        // TODO: Check if invStack's trait is anywhere in rStack's traits
        // if (getTrait(rStack, trait) == null || getTrait(invStack, trait) == null)
        //     return false;                                                   // If one of the bees are missing the properly formatted species tag, they don't match
        return Arrays.equals(getTrait(rStack, trait, false), getTrait(invStack, trait, false));    // Return whether they match or not
    }

    public static boolean checkTraitIsInDatabase(EnumBeeChromosome chromosome, String trait) {
        for (Map.Entry<Integer, String> entry : getAllelesForChromosome(chromosome).entrySet()) {
            if (trait.equals(entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<String> getBeeInfo(ItemStack bee) {
        ArrayList<String> info = new ArrayList<>();

        // TODO: #16 Sometimes bees are showing the wrong DisplayNames (but correct species tags?)
        info.add(getInfoString("bqforestry.label.bee.species", bee, EnumBeeChromosome.SPECIES));
        info.add(getInfoString("bqforestry.label.bee.lifespan", bee, EnumBeeChromosome.LIFESPAN));
        info.add(getInfoString("bqforestry.label.bee.speeds", bee, EnumBeeChromosome.SPEED));
        info.add(getInfoString("bqforestry.label.bee.flowering", bee, EnumBeeChromosome.FLOWERING));
        info.add(getInfoString("bqforestry.label.bee.fertility", bee, EnumBeeChromosome.FERTILITY));
        info.add(getInfoString("bqforestry.label.bee.territory", bee, EnumBeeChromosome.TERRITORY));
        info.add(getInfoString("bqforestry.label.bee.effect", bee, EnumBeeChromosome.EFFECT));
        info.add(getInfoString("bqforestry.label.bee.temp", bee, EnumBeeChromosome.TEMPERATURE_TOLERANCE));
        info.add(getInfoString("bqforestry.label.bee.humidity", bee, EnumBeeChromosome.HUMIDITY_TOLERANCE));
        info.add(getInfoString("bqforestry.label.bee.sleeps", bee, EnumBeeChromosome.NEVER_SLEEPS));
        info.add(getInfoString("bqforestry.label.bee.rain", bee, EnumBeeChromosome.TOLERATES_RAIN));
        info.add(getInfoString("bqforestry.label.bee.dwelling", bee, EnumBeeChromosome.CAVE_DWELLING));
        info.add(getInfoString("bqforestry.label.bee.flowers", bee, EnumBeeChromosome.FLOWER_PROVIDER));
        return info;
    }

    private static String getInfoString(String translationKey, ItemStack bee, EnumBeeChromosome chromosome) {
        String GOLD = TextFormatting.GOLD.toString();
        String AQUA = TextFormatting.AQUA.toString();
        String DIV = GOLD.concat(", ").concat(AQUA);

        StringUtils.IStringStyle style = (str) -> str.substring(indexOfFirstCapital(str));
        String ret = GOLD.concat(QuestTranslation.translate(translationKey)).concat(": ").concat(AQUA);

        if (chromosome == EnumBeeChromosome.SPECIES) {
            return ret.concat(getDisplayName(bee)).concat(" (" + getTrait(bee, chromosome, true)[0]) + ")";
        }

        return ret.concat(flattenArray(getTrait(bee, chromosome, false), DIV, style));
    }

    public static void listAllSpecies() {
        BQ_Forestry.log.info("Config ListAllBees is TRUE. Outputting bees list now.");
        BQ_Forestry.log.info("===========================================================");

        Collection<IAllele> species = AlleleManager.alleleRegistry.getRegisteredAlleles(EnumBeeChromosome.SPECIES);
        Iterator a = species.iterator();
        for (int i = 0; i < species.size(); i++) {
            String spe = a.next().toString();
            BQ_Forestry.log.info(String.format("Bees species found: %1$d / %2$d - %3$s", i + 1, species.size(), spe));
        }

        BQ_Forestry.log.info("===========================================================");
    }
}
