package net.reikeb.maxilib;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.reikeb.maxilib.recipes.ShapedToolRecipe;
import net.reikeb.maxilib.recipes.ShapelessToolRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MaxiLib.MODID)
public class MaxiLib {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "maxilib";

    public MaxiLib() {

        // Registers an event with the mod specific event bus. This is needed to register new stuff.
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(RecipeSerializer.class, this::registerRecipeSerializers);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {

        // Register the recipe serializer. This handles from json, from packet, and to packet.
        event.getRegistry().register(ShapedToolRecipe.SERIALIZER);
        event.getRegistry().register(ShapelessToolRecipe.SERIALIZER);
    }
}
