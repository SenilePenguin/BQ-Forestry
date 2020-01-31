package com.nicjames2378.bqforestry.tasks;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.IQuest;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.cache.CapabilityProviderQuestCache;
import betterquesting.api2.cache.QuestCache;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import com.nicjames2378.bqforestry.BQ_Forestry;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.GuiEditTaskBeeRetrieval;
import com.nicjames2378.bqforestry.client.tasks.PanelTaskForestryRetrieval;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import com.nicjames2378.bqforestry.tasks.factory.FactoryTaskForestryRetrieval;
import com.nicjames2378.bqforestry.utils.Reference;
import com.nicjames2378.bqforestry.utils.UtilitiesBee;
import forestry.api.apiculture.EnumBeeChromosome;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.nicjames2378.bqforestry.utils.UtilitiesBee.*;

public class TaskForestryRetrieval implements ITaskInventory { //}, IItemTask {
    public static BigItemStack getDefaultBee() {
        return new BigItemStack(getBaseBee(UtilitiesBee.DEFAULT_SPECIES, UtilitiesBee.BeeTypes.valueOf(ConfigHandler.cfgBeeType), ConfigHandler.cfgOnlyMated));
    }

    public final NonNullList<BigItemStack> requiredItems = new NonNullList<BigItemStack>() {
        {
            add(getDefaultBee());
        }
    };
    private final Set<UUID> completeUsers = new TreeSet<>();
    private final HashMap<UUID, int[]> userProgress = new HashMap<>();
    public boolean consume = ConfigHandler.cfgConsume;
    public boolean autoConsume = ConfigHandler.cfgAutoConsume;

    @Override
    public String getUnlocalisedName() {
        return Reference.MOD_ID + ".task.bee_retrieval";
    }

    @Override
    public ResourceLocation getFactoryID() {
        return FactoryTaskForestryRetrieval.INSTANCE.getRegistryName();
    }

    @Override
    public boolean isComplete(UUID uuid) {
        return completeUsers.contains(uuid);
    }

    @Override
    public void setComplete(UUID uuid) {
        completeUsers.add(uuid);
    }

    @Override
    public void onInventoryChange(@Nonnull DBEntry<IQuest> quest, @Nonnull EntityPlayer player) {
        if (!consume || autoConsume) {
            detect(new ParticipantInfo(player), quest);
        }
    }

    @Override
    public void detect(ParticipantInfo participant, DBEntry<IQuest> quest) {
//    public void detect(EntityPlayer player, IQuest quest) {
        UUID playerID = QuestingAPI.getQuestingUUID(participant.PLAYER);

        if (participant.PLAYER.inventory == null || isComplete(playerID)) return;

        int[] progress = this.getUsersProgress(playerID);
        boolean updated = false;

        /*if(!consume)
        {
            if(groupDetect) // Reset all detect progress
            {
                Arrays.fill(progress, 0);
            } else
            {
                for(int i = 0; i < progress.length; i++)
                {
                    if(progress[i] != 0 && progress[i] < requiredItems.get(i).stackSize) // Only reset progress for incomplete entries
                    {
                        progress[i] = 0;
                        updated = true;
                    }
                }
            }
        }*/

        // Iterate the player's inventory slots
        for (int i = 0; i < participant.PLAYER.inventory.getSizeInventory(); i++) {
            ItemStack stack = participant.PLAYER.inventory.getStackInSlot(i);

            // Jump the loop if the slot is empty
            if (stack.isEmpty()) continue;
            int remStack = stack.getCount(); // Allows the stack detection to split across multiple requirements

            // Iterate through the required items
            for (int j = 0; j < requiredItems.size(); j++) {
                BigItemStack rStack = requiredItems.get(j);

                // If the item isn't even the correct item (DERP!)
                if (rStack.getBaseStack().isItemEqual(stack)) continue;

                // What does this do???
                if (progress[j] >= rStack.stackSize) continue;

                // If we require mated and the player's item isn't
                if (isMated(rStack.getBaseStack()) && !isMated(stack))
                    continue;

                // Gets a list of valid alleles from the quest bee item
                HashMap<EnumBeeChromosome, HashSet<String>> map = getAllTraits(rStack.getBaseStack(), false);
                boolean nbtIsValid = false;

                BQ_Forestry.debug("================");
                for (Map.Entry<EnumBeeChromosome, HashSet<String>> entry : map.entrySet()) {
                    BQ_Forestry.debug(String.format("SubmitItemDEBUG: Chromosome %1$s", entry.getKey()));

                    for (String s : entry.getValue()) {
                        BQ_Forestry.debug(String.format("                      Value %1$s", s));
                    }
                }
                BQ_Forestry.debug("");

                // Iterates through all valid alleles
                for (Map.Entry<EnumBeeChromosome, HashSet<String>> entry : map.entrySet()) {
                    // Safe to use [0] here since we don't currently support secondary traits anywhere else
                    String submissionTrait = getTrait(stack, entry.getKey(), true)[0];
                    BQ_Forestry.debug(String.format("SubmitItem: Chromosome %1$s (Wanted: %2$s)", entry.getKey(), entry.getValue()));

                    // If the bee's trait is anywhere in the set, carry on. Otherwise break the loop.
                    if (entry.getValue().contains(submissionTrait)) {
                        BQ_Forestry.debug(String.format("                      Value [%1$s] is VALID", submissionTrait));
                        // Set flag saying the NBT is still valid
                        nbtIsValid = true;
                    } else { // The trait was not found in the compatible traits list. Abort!
                        BQ_Forestry.debug(String.format("                      Value [%1$s] is INVALID", submissionTrait));
                        nbtIsValid = false;
                        break; // If we didn't find a valid allele for the required category, stop looping
                    }
                }

                // Did we have at least one NBT from each list of required values?
                if (nbtIsValid) {
                    int remaining = rStack.stackSize - progress[j];

                    // Are we taking items from the player for this quest?
                    if (consume) {
                        ItemStack removed = participant.PLAYER.inventory.decrStackSize(i, remaining);
                        progress[j] += removed.getCount();
                    } else {
                        int temp = Math.min(remaining, remStack);
                        remStack -= temp;
                        progress[j] += temp;
                    }

                    // Set a flag saying progress was made
                    updated = true;
                }
            }
        }

        // If progress was made, save it to Better Questing
        if (updated) setUserProgress(playerID, progress);

        boolean hasAll = true; //flag
        int[] totalProgress = getUsersProgress(playerID);

        for (int j = 0; j < requiredItems.size(); j++) {
            BigItemStack rStack = requiredItems.get(j);

            if (totalProgress[j] >= rStack.stackSize) continue;

            hasAll = false;
            break;
        }

        if (hasAll) {
            setComplete(playerID);
            updated = true;
        }

        if (updated) {
            QuestCache qc = participant.PLAYER.getCapability(CapabilityProviderQuestCache.CAP_QUEST_CACHE, null);
            if (qc != null) qc.markQuestDirty(QuestingAPI.getAPI(ApiReference.QUEST_DB).getID(quest.getValue()));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound json) {
        json.setBoolean("consume", consume);
        json.setBoolean("autoConsume", autoConsume);

        NBTTagList itemArray = new NBTTagList();
        for (BigItemStack stack : this.requiredItems) {
            itemArray.appendTag(JsonHelper.ItemStackToJson(stack, new NBTTagCompound()));
        }
        json.setTag("requiredItems", itemArray);

        return json;
    }

    @Override
    public void readFromNBT(NBTTagCompound json) {
        consume = json.getBoolean("consume");
        autoConsume = json.getBoolean("autoConsume");

        requiredItems.clear();
        NBTTagList iList = json.getTagList("requiredItems", 10);
        for (int i = 0; i < iList.tagCount(); i++) {
            requiredItems.add(JsonHelper.JsonToItemStack(iList.getCompoundTagAt(i)));
        }
    }

    @Override
    public void readProgressFromNBT(NBTTagCompound nbt, boolean merge) {
        if (!merge) {
            completeUsers.clear();
            userProgress.clear();
        }

        NBTTagList cList = nbt.getTagList("completeUsers", 8);
        for (int i = 0; i < cList.tagCount(); i++) {
            try {
                completeUsers.add(UUID.fromString(cList.getStringTagAt(i)));
            } catch (Exception e) {
                BQ_Forestry.log.log(Level.ERROR, "Unable to load UUID for task", e);
            }
        }

        NBTTagList pList = nbt.getTagList("userProgress", 10);
        for (int n = 0; n < pList.tagCount(); n++) {
            try {
                NBTTagCompound pTag = pList.getCompoundTagAt(n);
                UUID uuid = UUID.fromString(pTag.getString("uuid"));

                int[] data = new int[requiredItems.size()];
                NBTTagList dNbt = pTag.getTagList("data", 3);
                for (int i = 0; i < data.length && i < dNbt.tagCount(); i++) {
                    data[i] = dNbt.getIntAt(i);
                }

                userProgress.put(uuid, data);
            } catch (Exception e) {
                BQ_Forestry.log.log(Level.ERROR, "Unable to load user progress for task", e);
            }
        }
    }

    @Override
    public NBTTagCompound writeProgressToNBT(NBTTagCompound nbt, @Nullable List<UUID> users) {
        NBTTagList jArray = new NBTTagList();
        NBTTagList progArray = new NBTTagList();

        if (users != null) {
            users.forEach((uuid) -> {
                if (completeUsers.contains(uuid)) jArray.appendTag(new NBTTagString(uuid.toString()));

                int[] data = userProgress.get(uuid);
                if (data != null) {
                    NBTTagCompound pJson = new NBTTagCompound();
                    pJson.setString("uuid", uuid.toString());
                    NBTTagList pArray = new NBTTagList();
                    for (int i : data) pArray.appendTag(new NBTTagInt(i));
                    pJson.setTag("data", pArray);
                    progArray.appendTag(pJson);
                }
            });
        } else {
            completeUsers.forEach((uuid) -> jArray.appendTag(new NBTTagString(uuid.toString())));

            userProgress.forEach((uuid, data) -> {
                NBTTagCompound pJson = new NBTTagCompound();
                pJson.setString("uuid", uuid.toString());
                NBTTagList pArray = new NBTTagList();
                for (int i : data) pArray.appendTag(new NBTTagInt(i));
                pJson.setTag("data", pArray);
                progArray.appendTag(pJson);
            });
        }

        nbt.setTag("completeUsers", jArray);
        nbt.setTag("userProgress", progArray);

        return nbt;
    }

    @Override
    public void resetUser(@Nullable UUID uuid) {
        if (uuid == null) {
            completeUsers.clear();
            userProgress.clear();
        } else {
            completeUsers.remove(uuid);
            userProgress.remove(uuid);
        }
    }

    @Override
    public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest) {
        return new PanelTaskForestryRetrieval(rect, this);
    }

    /*@Override
    public boolean canAcceptItem(UUID owner, IQuest quest, ItemStack stack) {
        Main.log.info("WHATISWHERE - CanAccept");
        if (owner == null || stack == null || stack.isEmpty() || !consume || isComplete(owner) || requiredItems.size() <= 0)
            return false;

        int[] progress = getUsersProgress(owner);

        for (int j = 0; j < requiredItems.size(); j++) {
            BigItemStack rStack = requiredItems.get(j);

            if (progress[j] >= rStack.stackSize) continue;
            if (isMated(rStack.getBaseStack()) && !isMated(stack)) continue;

            if (checkTraitsMatch(rStack.getBaseStack(), stack, EnumBeeChromosome.SPECIES)) return true;
        }

        return false;
    }


    @Override
    public ItemStack submitItem(UUID owner, IQuest quest, ItemStack input) {
        Main.log.info("WHATISWHERE - Submit");
        if (owner == null || input.isEmpty() || !consume || isComplete(owner)) return input;

        ItemStack stack = input.copy();

        int[] progress = getUsersProgress(owner);
        boolean updated = false;

        for (int j = 0; j < requiredItems.size(); j++) {
            if (stack.isEmpty()) break;

            BigItemStack rStack = requiredItems.get(j);

            if (progress[j] >= rStack.stackSize) continue;

            int remaining = rStack.stackSize - progress[j];


            // Iterate all chromosomes on rStack
            // Iterate through all traits on chromosome
            // Check if stack contains at least one of them
            // If not, break. Otherwise, continue checking.

            boolean isValid = false;


            Main.log.info("================");
            HashMap<EnumBeeChromosome, HashSet<String>> map = getAllTraits(rStack.getBaseStack(), false);
            for (Map.Entry<EnumBeeChromosome, HashSet<String>> entry : map.entrySet()) {
                Main.log.info(String.format("SubmitItemDEBUG: Chromosome %1$s", entry.getValue()));

                for (String s : entry.getValue()) {
                    Main.log.info(String.format("                      Value %1$s", s));
                }
            }
            Main.log.info("");


            // getAllTraits returns a map with only chromosomes containing traits
            for (Map.Entry<EnumBeeChromosome, HashSet<String>> entry : map.entrySet()) {
                // Safe to use [0] here since we don't currently support secondary traits anywhere else
                String stackTrait = getTrait(stack, entry.getKey(), false)[0];
                Main.log.info(String.format("SubmitItem: Chromosome %1$s, stackTrait %2$s", entry.getValue(), stackTrait));


                // If the bee's trait is anywhere in the set, carry on. Otherwise break the loop.
                if (entry.getValue().contains(stackTrait)) {
                    Main.log.info("                      IsContained? TRUE");
                    isValid = true;
                    continue;
                }
                Main.log.info("                      IsContained? FALSE");
                isValid = false;
                break;
            }

            if (isValid) { //checkTraitsMatch(rStack.getBaseStack(), stack, EnumBeeChromosome.SPECIES)) {
                int removed = Math.min(stack.getCount(), remaining);
                stack.shrink(removed);
                progress[j] += removed;
                updated = true;
                if (stack.isEmpty()) break;
            }
        }

        if (updated) {
            setUserProgress(owner, progress);
        }

        return stack.isEmpty() ? ItemStack.EMPTY : stack;
    }*/

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest) {
        return new GuiEditTaskBeeRetrieval(parent, quest.getValue(), this);
    }

    public void setUserProgress(UUID uuid, int[] progress) {
        userProgress.put(uuid, progress);
    }

    public int[] getUsersProgress(UUID uuid) {
        int[] progress = userProgress.get(uuid);
        return progress == null || progress.length != requiredItems.size() ? new int[requiredItems.size()] : progress;
    }
/*
    private void bulkMarkDirty(@Nonnull List<UUID> uuids, int questID) {
        if (uuids.size() <= 0) return;
        final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        uuids.forEach((value) -> {
            EntityPlayerMP player = server.getPlayerList().getPlayerByUUID(value);
            //noinspection ConstantConditions
            if (player == null) return;
            QuestCache qc = player.getCapability(CapabilityProviderQuestCache.CAP_QUEST_CACHE, null);
            if (qc != null) qc.markQuestDirty(questID);
        });
    }

    private List<Tuple<UUID, int[]>> getBulkProgress(@Nonnull List<UUID> uuids) {
        if (uuids.size() <= 0) return Collections.emptyList();
        List<Tuple<UUID, int[]>> list = new ArrayList<>();
        uuids.forEach((key) -> list.add(new Tuple<>(key, getUsersProgress(key))));
        return list;
    }

    private void setBulkProgress(@Nonnull List<Tuple<UUID, int[]>> list) {
        list.forEach((entry) -> setUserProgress(entry.getFirst(), entry.getSecond()));
    }*/
}
