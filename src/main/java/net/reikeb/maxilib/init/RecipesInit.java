package net.reikeb.maxilib.init;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.reikeb.maxilib.MaxiLib;
import net.reikeb.maxilib.recipes.ShapedToolRecipe;
import net.reikeb.maxilib.recipes.ShapelessToolRecipe;

public class RecipesInit {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MaxiLib.MODID);

    public static final RegistryObject<ShapedToolRecipe.Serializer> SHAPED_TOOL_RECIPE_SERIALIZE = RECIPE_SERIALIZERS.register("tool_shaped", ShapedToolRecipe.Serializer::new);
    public static final RegistryObject<ShapelessToolRecipe.Serializer> SHAPELESS_TOOL_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("tool_shapeless", ShapelessToolRecipe.Serializer::new);
}
