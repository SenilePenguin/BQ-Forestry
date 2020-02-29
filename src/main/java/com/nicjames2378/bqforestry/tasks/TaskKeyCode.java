package com.nicjames2378.bqforestry.tasks;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import com.nicjames2378.bqforestry.BQ_Forestry;
import com.nicjames2378.bqforestry.client.gui.editors.tasks.GuiEditTaskKeyCode;
import com.nicjames2378.bqforestry.client.tasks.PanelTaskKeyCode;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import com.nicjames2378.bqforestry.tasks.factory.FactoryTaskKeyCode;
import com.nicjames2378.bqforestry.utils.Reference;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class TaskKeyCode implements ITask {
    private final Set<UUID> completeUsers = new TreeSet<>();
    public String keyCode = generateRandomString(8, true, true);
    public String inputCode = "";
    public boolean caseSensitive = ConfigHandler.cfgCaseSensitiveKeyCodes;

    private static String generateRandomString(int length, boolean allowLetters, boolean allowNumbers) {
        return RandomStringUtils.random(length, allowLetters, allowNumbers);
    }

    @Override
    public String getUnlocalisedName() {
        return Reference.MOD_ID + ".task.keycode";
    }

    @Override
    public ResourceLocation getFactoryID() {
        return FactoryTaskKeyCode.INSTANCE.getRegistryName();
    }

    @Override
    public void detect(ParticipantInfo participant, DBEntry<IQuest> quest) {
        UUID playerID = QuestingAPI.getQuestingUUID(participant.PLAYER);

        if (isComplete(playerID)) return;
        if (caseSensitive) {
            if (inputCode.equals(keyCode)) setComplete(playerID);
        } else {
            if (inputCode.equalsIgnoreCase(keyCode)) setComplete(playerID);
        }
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
    public NBTTagCompound writeToNBT(NBTTagCompound json) {
        json.setString("keycode", keyCode);
        json.setBoolean("caseSensitive", caseSensitive);
        return json;
    }

    @Override
    public void readFromNBT(NBTTagCompound json) {
        keyCode = json.getString("keycode");
        caseSensitive = json.getBoolean("caseSensitive");
    }

    @Override
    public void readProgressFromNBT(NBTTagCompound nbt, boolean merge) {
        if (!merge) {
            completeUsers.clear();
        }

        NBTTagList cList = nbt.getTagList("completeUsers", 8);
        for (int i = 0; i < cList.tagCount(); i++) {
            try {
                completeUsers.add(UUID.fromString(cList.getStringTagAt(i)));
            } catch (Exception e) {
                BQ_Forestry.log.log(Level.ERROR, "Unable to load UUID for task", e);
            }
        }
    }

    @Override
    public NBTTagCompound writeProgressToNBT(NBTTagCompound nbt, @Nullable List<UUID> users) {
        NBTTagList jArray = new NBTTagList();

        if (users != null) {
            users.forEach((uuid) -> {
                if (completeUsers.contains(uuid)) jArray.appendTag(new NBTTagString(uuid.toString()));
            });
        } else {
            completeUsers.forEach((uuid) -> jArray.appendTag(new NBTTagString(uuid.toString())));
        }
        nbt.setTag("completeUsers", jArray);
        return nbt;
    }

    @Override
    public void resetUser(@Nullable UUID uuid) {
        if (uuid == null) {
            completeUsers.clear();
        } else {
            completeUsers.remove(uuid);
        }
    }

    @Override
    public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest) {
        return new PanelTaskKeyCode(rect, quest.getValue(), this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest) {
        return new GuiEditTaskKeyCode(parent, quest, this);
    }
}
