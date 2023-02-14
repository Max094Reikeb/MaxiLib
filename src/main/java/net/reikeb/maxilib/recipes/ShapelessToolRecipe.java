package net.reikeb.maxilib.recipes;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.RecipeMatcher;

public class ShapelessToolRecipe implements CraftingRecipe {

    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation id;
    final String group;
    final CraftingBookCategory category;
    final NonNullList<Ingredient> ingredients;
    final ItemStack result;
    final NonNullList<Ingredient> unconsumedItems;
    private final boolean isSimple;

    public ShapelessToolRecipe(ResourceLocation resourceLocation, String group, CraftingBookCategory category, NonNullList<Ingredient> ingredients, ItemStack result, NonNullList<Ingredient> unconsumed) {
        this.id = resourceLocation;
        this.group = group;
        this.category = category;
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

    public CraftingBookCategory category() {
        return this.category;
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

    public static class Serializer implements RecipeSerializer<ShapelessToolRecipe> {

        public ShapelessToolRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            String s = GsonHelper.getAsString(jsonObject, "group", "");
            CraftingBookCategory craftingBookCategory = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(jsonObject, "category", null), CraftingBookCategory.MISC);
            NonNullList<Ingredient> list = ShapedToolRecipe.ingredientFromJson(GsonHelper.getAsJsonArray(jsonObject, "ingredients"));
            ItemStack itemStack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
            NonNullList<Ingredient> unconsumedItems = ShapedToolRecipe.ingredientFromJson(GsonHelper.getAsJsonArray(jsonObject, "unconsumed"));
            return new ShapelessToolRecipe(resourceLocation, s, craftingBookCategory, list, itemStack, unconsumedItems);
        }

        public ShapelessToolRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            String s = friendlyByteBuf.readUtf();
            CraftingBookCategory craftingBookCategory = friendlyByteBuf.readEnum(CraftingBookCategory.class);
            int i = friendlyByteBuf.readVarInt();
            int size = friendlyByteBuf.readVarInt();
            NonNullList<Ingredient> list = NonNullList.withSize(i, Ingredient.EMPTY);

            list.replaceAll(ignored -> Ingredient.fromNetwork(friendlyByteBuf));

            ItemStack itemStack = friendlyByteBuf.readItem();
            NonNullList<Ingredient> unconsumed = NonNullList.withSize(size, Ingredient.EMPTY);

            unconsumed.replaceAll(ignored -> Ingredient.fromNetwork(friendlyByteBuf));
            return new ShapelessToolRecipe(resourceLocation, s, craftingBookCategory, list, itemStack, unconsumed);
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
