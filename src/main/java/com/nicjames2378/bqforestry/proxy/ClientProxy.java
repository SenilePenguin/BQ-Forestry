package com.nicjames2378.bqforestry.proxy;

import com.nicjames2378.bqforestry.BQ_Forestry;
import com.nicjames2378.bqforestry.client.themes.ThemeHandler;
import com.nicjames2378.bqforestry.utils.Reference;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Side.CLIENT)

public class ClientProxy extends CommonProxy {
    @Override
    public void registerExpansion() {
        super.registerExpansion();
    }

    @Override
    // Only register the theme information on the client side
    public void registerTheme() {
        BQ_Forestry.debug("Registering Themes!");
        ThemeHandler.registerTextures();
    }
}
