package com.nicjames2378.bqforestry.client.gui;

import com.nicjames2378.bqforestry.config.ConfigHandler;
import com.nicjames2378.bqforestry.utils.Reference;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiBQFConfig extends GuiConfig {

    public GuiBQFConfig(GuiScreen parent) {
        super(parent, getCategories(ConfigHandler.config), Reference.MOD_ID, false, false, Reference.NAME);
    }

    public static List<IConfigElement> getCategories(Configuration config) {
        List<IConfigElement> cats = new ArrayList<>();

        for (String s : config.getCategoryNames()) {
            cats.add(new ConfigElement(config.getCategory(s)));
        }

        return cats;
    }
}