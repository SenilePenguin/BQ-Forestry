package com.nicjames2378.bqforestry.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import com.nicjames2378.bqforestry.tasks.TaskKeyCode;
import com.nicjames2378.bqforestry.utils.Reference;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskKeyCode implements IFactoryData<ITask, NBTTagCompound> {

    public static final FactoryTaskKeyCode INSTANCE = new FactoryTaskKeyCode();

    @Override
    public TaskKeyCode loadFromData(NBTTagCompound json) {
        TaskKeyCode task = new TaskKeyCode();
        task.readFromNBT(json);
        return task;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(Reference.MOD_ID + ":keycode");
    }

    @Override
    public TaskKeyCode createNew() {
        return new TaskKeyCode();
    }
}
