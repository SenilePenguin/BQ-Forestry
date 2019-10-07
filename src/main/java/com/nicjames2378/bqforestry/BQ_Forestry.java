package com.nicjames2378.bqforestry;

import com.nicjames2378.bqforestry.commands.BQFCommandFindTrait;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import com.nicjames2378.bqforestry.proxy.CommonProxy;
import com.nicjames2378.bqforestry.utils.Reference;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = "@VERSION@", dependencies = Reference.DEPENDENCIES, guiFactory = Reference.GUI_FACTORY)
public class BQ_Forestry {

    @Instance
    public static BQ_Forestry instance;

    public static Logger log;
    public static boolean hasJEI = false;
    public static boolean hasForestry = false;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
    public static CommonProxy proxy;

    @EventHandler
    public static void PreInit(FMLPreInitializationEvent event) {
        log = event.getModLog();

        ConfigHandler.initialize(event.getSuggestedConfigurationFile());

        proxy.registerHandlers();
    }

    @EventHandler
    public static void Init(FMLInitializationEvent event) {
        hasJEI = Loader.isModLoaded("jei");
        hasForestry = Loader.isModLoaded("forestry");

        //proxy.Init(event);
    }

    @EventHandler
    public static void PostInit(FMLPostInitializationEvent event) {
        if (Loader.isModLoaded("betterquesting")) {
            proxy.registerExpansion();
        } else {
            // Should never be reached, but just in case...
            log.error("Better Questing not found! This mod requires it!");
        }

        proxy.doDebugOutputs();
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new BQFCommandFindTrait());
    }
}