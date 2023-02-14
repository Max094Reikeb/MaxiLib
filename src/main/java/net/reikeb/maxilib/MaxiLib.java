package net.reikeb.maxilib;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.reikeb.maxilib.init.RecipesInit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MaxiLib.MODID)
public class MaxiLib {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "maxilib";

    public MaxiLib() {

        // Register recipes
        RecipesInit.RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }
}
