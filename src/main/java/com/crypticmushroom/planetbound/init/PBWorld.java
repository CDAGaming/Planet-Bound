package com.crypticmushroom.planetbound.init;

import com.crypticmushroom.planetbound.world.WorldProviderRonne;
import com.crypticmushroom.planetbound.world.biome.BiomeRedDesert;
import com.crypticmushroom.planetbound.world.gen.PBOreGenerator;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@EventBusSubscriber
public class PBWorld {
    public static final int RONNE_ID = 4; //TODO make this configurable

    public static final DimensionType RONNE = DimensionType.register("ronne", "ronne", RONNE_ID, WorldProviderRonne.class, false);

    public static final BiomeRedDesert RED_DESERT = new BiomeRedDesert();

    public static void init() {
        GameRegistry.registerWorldGenerator(new PBOreGenerator(), 0);

        DimensionManager.registerDimension(RONNE_ID, RONNE);
    }

    @SubscribeEvent
    public static void registerBiomes(RegistryEvent.Register<Biome> event) {
        event.getRegistry().register(RED_DESERT);
    }
}