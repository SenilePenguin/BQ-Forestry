package com.nicjames2378.bqforestry;

import com.nicjames2378.bqforestry.commands.BQFCommandGetSpecies;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import com.nicjames2378.bqforestry.proxy.CommonProxy;
import com.nicjames2378.bqforestry.utils.Reference;
import com.nicjames2378.bqforestry.utils.UtilitiesBee;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY)
public class Main {

    @Instance
    public static Main instance;

    public static Logger log;
    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);
    public static boolean hasJEI = false;

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
        //proxy.Init(event);
    }

    @EventHandler
    public static void PostInit(FMLPostInitializationEvent event) {
        if (Loader.isModLoaded("betterquesting"))
            proxy.registerExpansion();

        hasJEI = Loader.isModLoaded("jei");

        if (ConfigHandler.cfgListBeeSpecies) UtilitiesBee.listBeeSpecies();
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new BQFCommandGetSpecies());
    }
}