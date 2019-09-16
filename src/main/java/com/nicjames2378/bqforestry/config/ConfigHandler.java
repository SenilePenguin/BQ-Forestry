package com.nicjames2378.bqforestry.config;

import com.nicjames2378.bqforestry.Main;
import com.nicjames2378.bqforestry.utils.Reference;
import com.nicjames2378.bqforestry.utils.UtilitiesBee;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

public class ConfigHandler {
    public static final ConfigHandler INSTANCE = new ConfigHandler();
    public static Configuration config;

    private static String key = "bqforestry.config.";
    private static String CATEGORY_FORESTRY = "forestry";
    private static String CATEGORY_OTHER = "other";
    private static String CATEGORY_DEBUG = "debug";
    private static int configVersion = 2;

    public static float cfgScrollSpeed = 2.0f;
    public static boolean cfgAutoConsume = false;
    public static boolean cfgConsume = false;
    public static boolean cfgOnlyMated = false;
    public static String cfgBeeType = UtilitiesBee.BeeTypes.princess.get();

    public static boolean cfgCaseSensitiveKeyCodes = true;

    public static boolean cfgListBeeSpecies = true;

    public static void initialize(File file) {
        config = new Configuration(file);
        loadVarsFromConfig();
        config.save();
    }

    public static void loadVarsFromConfig() {
        if (config == null) {
            Main.log.warn("Config attempted to be loaded before it was initialised!");
            return;
        }
        config.load();

        // ===== Handle Versioning Stuff =====
        // No versioning found
        if (!config.hasKey(CATEGORY_DEBUG, "version")) {
            Main.log.warn("Config Versioning information not found!");
        }
        // Incorrect versioning found
        if (config.getInt("version", CATEGORY_DEBUG, configVersion, configVersion, configVersion, "Versioning Information. Do not change!", key + "versioning") != configVersion) {
            Main.log.error("Incorrect versioning detected! Recommended to delete config and let it regenerate!");
        }

        // FORESTRY
        cfgScrollSpeed = config.getFloat("Scroll Speed", CATEGORY_FORESTRY, 2.0f, 0.5f, 4.0f, "How fast this mod handles scrolling in it's Quest Book pages.", key + "scrollspeed");
        cfgAutoConsume = config.getBoolean("Default AutoConsume", CATEGORY_FORESTRY, false, "The default value for 'autoconsume' on BQF tasks.", key + "autoconsume");
        cfgConsume = config.getBoolean("Default Consume", CATEGORY_FORESTRY, false, "The default value for 'consume' on BQF tasks.", key + "consume");
        cfgOnlyMated = config.getBoolean("Default OnlyMated", CATEGORY_FORESTRY, false, "The default value for 'onlyMated' on BQF tasks.", key + "onlymated");
        cfgBeeType = config.getString("Default Type", CATEGORY_FORESTRY, UtilitiesBee.BeeTypes.princess.get(), "The default selected bee type on BQF tasks.", UtilitiesBee.getAllTypes(), key + "beetype");

        //DEBUG
        cfgListBeeSpecies = config.getBoolean("ListAllBees", CATEGORY_DEBUG, true, "Should all bee species be printed to the log in startup?", key + "listbeespecies");

        // OTHER
        cfgCaseSensitiveKeyCodes = config.getBoolean("Case Sensitive Keycodes", CATEGORY_OTHER, true, "Should keycodes be case sensitive?", key + "casesensitivekeycodes");
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Reference.MOD_ID)) {
            config.save();
            loadVarsFromConfig();
        }
    }
}
