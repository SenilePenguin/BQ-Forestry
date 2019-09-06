package com.nicjames2378.bqforestry.proxy;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import betterquesting.api2.registry.IRegistry;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import com.nicjames2378.bqforestry.tasks.factory.FactoryTaskForestryRetrieval;
import com.nicjames2378.bqforestry.utils.Reference;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Side.SERVER)

public class CommonProxy {
    public void registerExpansion() {
        IRegistry<IFactoryData<ITask, NBTTagCompound>, ITask> taskReg = QuestingAPI.getAPI(ApiReference.TASK_REG);
        taskReg.register(FactoryTaskForestryRetrieval.INSTANCE);

        //IRegistry<IFactoryData<IReward, NBTTagCompound>, IReward> rewardReg = QuestingAPI.getAPI(ApiReference.REWARD_REG);
        //rewardReg.register(FactoryRewardItem.INSTANCE);
    }

    public void registerHandlers() {
        MinecraftForge.EVENT_BUS.register(ConfigHandler.INSTANCE);
    }
}
