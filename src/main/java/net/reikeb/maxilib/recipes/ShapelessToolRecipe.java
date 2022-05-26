package net.reikeb.maxilib.recipes;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.reikeb.maxilib.MaxiLib;

public class ShapelessToolRecipe implements CraftingRecipe {

    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation id;
    final String group;
    final NonNullList<Ingredient> ingredients;
    final ItemStack result;
    final NonNullList<Ingredient> unconsumedItems;
    private final boolean isSimple;

    public ShapelessToolRecipe(ResourceLocation resourceLocation, String group, NonNullList<Ingredient> ingredients, ItemStack result, NonNullList<Ingredient> unconsumed) {
        this.id = resourceLocation;
        this.group = group;
        this.ingredients = ingredients;
        this.result = result;
        this.unconsumedItems = unconsumed;
        this.isSimple = ingredients.stream().allMatch(Ingredient::isSimple);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public String getGroup() {
        return this.group;
    }

    public ItemStack getResultItem() {
        return this.result;
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        StackedContents stackedContents = new StackedContents();
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
        int i = 0;

        for (int j = 0; j < container.getContainerSize(); ++j) {
            ItemStack itemStack = container.getItem(j);
            if (!itemStack.isEmpty()) {
                ++i;
                if (isSimple)
                    stackedContents.accountStack(itemStack, 1);
                else inputs.add(itemStack);
            }
        }

        return ShapedToolRecipe.checkUnconsumedItems(container, this.unconsumedItems) && i == this.ingredients.size()
                && (isSimple ? stackedContents.canCraft(this, null) : RecipeMatcher.findMatches(inputs, this.ingredients) != null);
    }

    public ItemStack assemble(CraftingContainer container) {
        return this.result.copy();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        return ShapedToolRecipe.getRemainingUnconsumedItems(container, this.unconsumedItems);
    }

    public boolean canCraftInDimensions(int dim1, int dim2) {
        return dim1 * dim2 >= this.ingredients.size();
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<ShapelessToolRecipe> {

        Serializer() {
            this.setRegistryName(new ResourceLocation(MaxiLib.MODID, "tool_shapeless"));
        }

        public ShapelessToolRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            String s = GsonHelper.getAsString(jsonObject, "group", "");
            NonNullList<Ingredient> list = ShapedToolRecipe.ingredientFromJson(GsonHelper.getAsJsonArray(jsonObject, "ingredients"));
            ItemStack itemStack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
            NonNullList<Ingredient> unconsumedItems = ShapedToolRecipe.ingredientFromJson(GsonHelper.getAsJsonArray(jsonObject, "unconsumed"));
            return new ShapelessToolRecipe(resourceLocation, s, list, itemStack, unconsumedItems);
        }

        public ShapelessToolRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            String s = friendlyByteBuf.readUtf();
            int i = friendlyByteBuf.readVarInt();
            int size = friendlyByteBuf.readVarInt();
            NonNullList<Ingredient> list = NonNullList.withSize(i, Ingredient.EMPTY);

            list.replaceAll(ignored -> Ingredient.fromNetwork(friendlyByteBuf));

            ItemStack itemStack = friendlyByteBuf.readItem();
            NonNullList<Ingredient> unconsumed = NonNullList.withSize(size, Ingredient.EMPTY);

            unconsumed.replaceAll(ignored -> Ingredient.fromNetwork(friendlyByteBuf));
            return new ShapelessToolRecipe(resourceLocation, s, list, itemStack, unconsumed);
        }

        public void toNetwork(FriendlyByteBuf friendlyByteBuf, ShapelessToolRecipe recipe) {
            friendlyByteBuf.writeUtf(recipe.group);
            friendlyByteBuf.writeVarInt(recipe.ingredients.size());
            friendlyByteBuf.writeVarInt(recipe.unconsumedItems.size());

            for (Ingredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(friendlyByteBuf);
            }

            for (Ingredient unconsumedItem : recipe.unconsumedItems) {
                unconsumedItem.toNetwork(friendlyByteBuf);
            }

            friendlyByteBuf.writeItem(recipe.result);
        }
    }
}
