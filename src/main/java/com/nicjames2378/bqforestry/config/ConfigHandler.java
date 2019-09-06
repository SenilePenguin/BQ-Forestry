package com.nicjames2378.bqforestry.config;

import com.nicjames2378.bqforestry.Main;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
    public static Configuration config;

    private static String key = "bqforestry.config.";
    private static String CATEGORY_EDITMODE = "editmode";

    public static float scrollSpeed = 2.0f;
    public static boolean cfgAutoConsume = false;
    public static boolean cfgConsume = false;
    public static boolean cfgOnlyMated = false;
    public static String cfgBeeType = ConfigHelper.BeeTypes.princess.get();

    public static void initConfigs() {
        if (config == null) {
            Main.log.error("Config attempted to be loaded before it was initialised!");
            return;
        }

        config.load();

        scrollSpeed = config.getFloat("Scroll Speed", CATEGORY_EDITMODE, 2.0f, 0.5f, 4.0f, "How fast this mod handles scrolling in it's Quest Book pages.", key + "scrollspeed");
        cfgAutoConsume = config.getBoolean("Default AutoConsume", CATEGORY_EDITMODE, false, "The default value for 'autoconsume' on BQF tasks.", key + "autoconsume");
        cfgConsume = config.getBoolean("Default Consume", CATEGORY_EDITMODE, false, "The default value for 'consume' on BQF tasks.", key + "consume");
        cfgOnlyMated = config.getBoolean("Default OnlyMated", CATEGORY_EDITMODE, false, "The default value for 'onlyMated' on BQF tasks.", key + "onlymated");
        cfgBeeType = config.getString("Default Beeess", CATEGORY_EDITMODE, ConfigHelper.BeeTypes.drone.get(), "The default selected bee type on BQF tasks.", ConfigHelper.getBeeTypes(), key + "beetype");

        config.save();
    }
}
