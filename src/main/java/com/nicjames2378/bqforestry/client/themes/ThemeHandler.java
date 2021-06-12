package com.nicjames2378.bqforestry.client.themes;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.resources.textures.SimpleTexture;
import betterquesting.api2.client.gui.resources.textures.SlicedTexture;
import betterquesting.api2.client.gui.themes.IThemeRegistry;
import betterquesting.client.themes.ThemeRegistry;
import com.nicjames2378.bqforestry.utils.Reference;
import net.minecraft.util.ResourceLocation;

public enum ThemeHandler {
    ITEM_FRAME("frame_item"),
    ITEM_FRAME_SELECTED("frame_item_selected"),
    ICON_ITEM_ADD("icon_item_add"),
    ICON_ITEM_REMOVE("icon_item_remove"),
    ICON_GENOME_SPECIES("icon_genome_species"),
    ICON_GENOME_SPEED("icon_genome_speed"),
    ICON_GENOME_LIFESPAN("icon_genome_liefspan"),
    ICON_GENOME_FERTILITY("icon_genome_fertility"),
    ICON_GENOME_TEMPERATURE_TOLERANCE("icon_genome_temperature_tolerance"),
    ICON_GENOME_NEVER_SLEEPS("icon_genome_never_sleeps"),
    ICON_GENOME_HUMIDITY_TOLERANCE("icon_genome_humidity_tolerance"),
    ICON_GENOME_RAIN_TOLERANCE("icon_genome_rain_tolerance"),
    ICON_GENOME_CAVE_DWELLING("icon_genome_cave_dwelling"),
    ICON_GENOME_FLOWER_PROVIDER("icon_genome_flower_provider"),
    ICON_GENOME_FLOWERING("icon_genome_flowering"),
    ICON_GENOME_TERRITORY("icon_genome_territory"),
    ICON_GENOME_EFFECT("icon_genome_effect"),
    ICON_GENOME_BEE_GROWTH("icon_genome_bee_growth"),
    ICON_CHECK_YES("icon_check_yes"),
    ICON_CHECK_NO("icon_check_no"),
    ICON_CHECK_BOTH("icon_check_both");

    public static final ResourceLocation TX_FRAMES = new ResourceLocation(Reference.MOD_ID, "textures/gui/frames.png");
    public static final ResourceLocation TX_ICONS = new ResourceLocation(Reference.MOD_ID, "textures/gui/buttons.png");
    private final ResourceLocation key;

    private ThemeHandler(String key) {
        this.key = new ResourceLocation(Reference.MOD_ID, key);
    }

    public static void registerTextures() {
        IThemeRegistry reg = QuestingAPI.getAPI(ApiReference.THEME_REG);
        reg.setDefaultTexture(ITEM_FRAME.key, new SlicedTexture(TX_FRAMES, new GuiRectangle(0, 0, 12, 12), new GuiPadding(1, 1, 1, 1)));
        reg.setDefaultTexture(ITEM_FRAME_SELECTED.key, new SlicedTexture(TX_FRAMES, new GuiRectangle(12, 0, 12, 12), new GuiPadding(1, 1, 1, 1)));
        reg.setDefaultTexture(ICON_ITEM_ADD.key, new SimpleTexture(TX_ICONS, new GuiRectangle(0, 0, 24, 24)).maintainAspect(true));
        reg.setDefaultTexture(ICON_GENOME_SPECIES.key, new SimpleTexture(TX_ICONS, new GuiRectangle(24, 0, 24, 24)).maintainAspect(true));
        reg.setDefaultTexture(ICON_ITEM_REMOVE.key, new SimpleTexture(TX_ICONS, new GuiRectangle(48, 0, 24, 24)).maintainAspect(true));
        reg.setDefaultTexture(ICON_GENOME_SPEED.key, new SimpleTexture(TX_ICONS, new GuiRectangle(72, 0, 24, 24)).maintainAspect(true));
        reg.setDefaultTexture(ICON_GENOME_LIFESPAN.key, new SimpleTexture(TX_ICONS, new GuiRectangle(96, 0, 24, 24)).maintainAspect(true));
        reg.setDefaultTexture(ICON_GENOME_FERTILITY.key, new SimpleTexture(TX_ICONS, new GuiRectangle(120, 0, 24, 24)).maintainAspect(true));
        reg.setDefaultTexture(ICON_GENOME_TEMPERATURE_TOLERANCE.key, new SimpleTexture(TX_ICONS, new GuiRectangle(144, 0, 24, 24)).maintainAspect(true));
        reg.setDefaultTexture(ICON_GENOME_NEVER_SLEEPS.key, new SimpleTexture(TX_ICONS, new GuiRectangle(168, 0, 24, 24)).maintainAspect(true));
        reg.setDefaultTexture(ICON_GENOME_HUMIDITY_TOLERANCE.key, new SimpleTexture(TX_ICONS, new GuiRectangle(192, 0, 24, 24)).maintainAspect(true));
        reg.setDefaultTexture(ICON_GENOME_RAIN_TOLERANCE.key, new SimpleTexture(TX_ICONS, new GuiRectangle(216, 0, 24, 24)).maintainAspect(true));
        reg.setDefaultTexture(ICON_GENOME_CAVE_DWELLING.key, new SimpleTexture(TX_ICONS, new GuiRectangle(0, 24, 24, 24)).maintainAspect(true));
        reg.setDefaultTexture(ICON_GENOME_FLOWER_PROVIDER.key, new SimpleTexture(TX_ICONS, new GuiRectangle(24, 24, 24, 24)).maintainAspect(true));
        reg.setDefaultTexture(ICON_GENOME_FLOWERING.key, new SimpleTexture(TX_ICONS, new GuiRectangle(48, 24, 24, 24)).maintainAspect(true));
        reg.setDefaultTexture(ICON_GENOME_TERRITORY.key, new SimpleTexture(TX_ICONS, new GuiRectangle(72, 24, 24, 24)).maintainAspect(true));
        reg.setDefaultTexture(ICON_GENOME_EFFECT.key, new SimpleTexture(TX_ICONS, new GuiRectangle(96, 24, 24, 24)).maintainAspect(true));
        reg.setDefaultTexture(ICON_GENOME_BEE_GROWTH.key, new SimpleTexture(TX_ICONS, new GuiRectangle(120, 24, 24, 24)).maintainAspect(true));

        reg.setDefaultTexture(ICON_CHECK_YES.key, new SimpleTexture(TX_ICONS, new GuiRectangle(0, 72, 24, 24)).maintainAspect(true));
        reg.setDefaultTexture(ICON_CHECK_NO.key, new SimpleTexture(TX_ICONS, new GuiRectangle(24, 72, 24, 24)).maintainAspect(true));
        reg.setDefaultTexture(ICON_CHECK_BOTH.key, new SimpleTexture(TX_ICONS, new GuiRectangle(48, 72, 24, 24)).maintainAspect(true));
    }

    public IGuiTexture getTexture() {
        return ThemeRegistry.INSTANCE.getTexture(this.key);
    }
}