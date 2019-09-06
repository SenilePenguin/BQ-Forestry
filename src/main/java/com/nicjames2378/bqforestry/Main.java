package com.nicjames2378.bqforestry;

import com.nicjames2378.bqforestry.commands.BQFCommandGetSpecies;
import com.nicjames2378.bqforestry.config.ConfigHandler;
import com.nicjames2378.bqforestry.proxy.CommonProxy;
import com.nicjames2378.bqforestry.utils.Reference;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import net.minecraftforge.common.config.Configuration;
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

import java.util.Collection;
import java.util.Iterator;

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

        ConfigHandler.config = new Configuration(event.getSuggestedConfigurationFile());
        ConfigHandler.initConfigs();
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

        listAllSpecies();
    }

    private static void listAllSpecies() {
        // TODO: Fix this message after implementing configs
        if (true) {//config.ListAllBees) {
            log.info("[NYI] Config ListAllBees is TRUE. Outputting bees list now.");
            log.info("===========================================================");

            Collection<IAllele> species = AlleleManager.alleleRegistry.getRegisteredAlleles(EnumBeeChromosome.SPECIES);
            Iterator a = species.iterator();
            for (int i = 0; i < species.size(); i++) {
                log.info(String.format("Bees species found: %1$d / %2$d - %3$s", i + 1, species.size(), a.next().toString()));
            }
            log.info("===========================================================");
        } // else logger.info("Config ListAllBees is FALSE. Skipping.");
    }

    // TODO: Implement Config: Scroll Speed
    // TODO: Implement Config: Default Options (consume/autoconsume)
    // TODO: Implement Config: Default Options (isMated)
    // TODO: Implement Config: Default Options (queen/princess/drone/larvae)
    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new BQFCommandGetSpecies());
    }
}