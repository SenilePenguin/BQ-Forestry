package com.nicjames2378.bqforestry.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import com.nicjames2378.bqforestry.tasks.TaskForestryRetrieval;
import com.nicjames2378.bqforestry.utils.Reference;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskForestryRetrieval implements IFactoryData<ITask, NBTTagCompound> {

    public static final FactoryTaskForestryRetrieval INSTANCE = new FactoryTaskForestryRetrieval();

    @Override
    public TaskForestryRetrieval loadFromData(NBTTagCompound json) {
        TaskForestryRetrieval task = new TaskForestryRetrieval();
        task.readFromNBT(json);
        return task;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(Reference.MOD_ID + ":forestryretrieval");
    }

    @Override
    public TaskForestryRetrieval createNew() {
        return new TaskForestryRetrieval();
    }
}
