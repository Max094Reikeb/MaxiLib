package net.reikeb.maxilib.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.reikeb.maxilib.MaxiLib;

import java.util.Map;

public class ShapedToolRecipe implements CraftingRecipe, IShapedRecipe<CraftingContainer> {

    public static final Serializer SERIALIZER = new Serializer();

    final int width;
    final int height;
    final NonNullList<Ingredient> recipeItems;
    final ItemStack result;
    final NonNullList<Ingredient> unconsumedItems;
    private final ResourceLocation id;
    final String group;
    final CraftingBookCategory category;

    public ShapedToolRecipe(ResourceLocation resourceLocation, String name, CraftingBookCategory category, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result, NonNullList<Ingredient> unconsumed) {
        this.id = resourceLocation;
        this.group = name;
        this.category = category;
        this.width = width;
        this.height = height;
        this.recipeItems = ingredients;
        this.result = result;
        this.unconsumedItems = unconsumed;
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
        return this.recipeItems;
    }

    public boolean canCraftInDimensions(int dim1, int dim2) {
        return dim1 >= this.width && dim2 >= this.height;
    }

    public boolean matches(CraftingContainer container, Level level) {
        for (int i = 0; i <= container.getWidth() - this.width; ++i) {
            for (int j = 0; j <= container.getHeight() - this.height; ++j) {
                if (this.matches(container, i, j, true)) {
                    return true;
                }

                if (this.matches(container, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matches(CraftingContainer container, int width, int height, boolean matches) {
        for (int i = 0; i < container.getWidth(); ++i) {
            for (int j = 0; j < container.getHeight(); ++j) {
                int k = i - width;
                int l = j - height;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < this.width && l < this.height) {
                    if (matches) {
                        ingredient = this.recipeItems.get(this.width - k - 1 + l * this.width);
                    } else {
                        ingredient = this.recipeItems.get(k + l * this.width);
                    }
                }

                if (!ingredient.test(container.getItem(i + j * container.getWidth()))) {
                    return false;
                }
            }
        }

        return checkUnconsumedItems(container, this.unconsumedItems);
    }

    static boolean checkUnconsumedItems(CraftingContainer container, NonNullList<Ingredient> unconsumedItems) {
        ItemStack itemStack = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack1 = container.getItem(i);
            if (!itemStack1.isEmpty()) {
                for (Ingredient ingredient : unconsumedItems) {
                    if (ingredient.test(itemStack1)) {
                        if (!itemStack.isEmpty()) {
                            return false;
                        }
                        itemStack = itemStack1;
                    }
                }
            }
        }
        return !itemStack.isEmpty();
    }

    public ItemStack assemble(CraftingContainer container) {
        return this.getResultItem().copy();
    }

    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        return getRemainingUnconsumedItems(container, this.unconsumedItems);
    }

    public static NonNullList<ItemStack> getRemainingUnconsumedItems(CraftingContainer container, NonNullList<Ingredient> unconsumedItems) {
        NonNullList<ItemStack> list = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < list.size(); ++i) {
            ItemStack itemStack = container.getItem(i);
            for (Ingredient ingredient : unconsumedItems) {
                if (ingredient.test(itemStack)) {
                    ItemStack itemStack1 = itemStack.copy();
                    itemStack1.setDamageValue(itemStack1.getDamageValue() + 10);
                    itemStack1.setCount(1);
                    if (itemStack1.getDamageValue() > 0) {
                        list.set(i, itemStack1);
                    }
                    break;
                }
            }
        }
        return list;
    }

    public int getWidth() {
        return this.width;
    }

    @Override
    public int getRecipeWidth() {
        return getWidth();
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    public int getRecipeHeight() {
        return getHeight();
    }

    public boolean isIncomplete() {
        NonNullList<Ingredient> list = this.getIngredients();
        return list.isEmpty() || list.stream().filter((p_151277_) ->
                !p_151277_.isEmpty()).anyMatch((p_151273_) ->
                p_151273_.getItems().length == 0);
    }

    public static NonNullList<Ingredient> ingredientFromJson(JsonArray jsonArray) {
        NonNullList<Ingredient> list = NonNullList.create();

        for (int i = 0; i < jsonArray.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(jsonArray.get(i));
            if (!ingredient.isEmpty()) {
                list.add(ingredient);
            }
        }

        if (list.isEmpty()) throw new JsonParseException("No ingradients for recipe");
        try {
            if (list.size() > ObfuscationReflectionHelper.findField(ShapedRecipe.class, "MAX_WIDTH").getInt(null) * ObfuscationReflectionHelper.findField(ShapedRecipe.class, "MAX_HEIGHT").getInt(null)) {
                throw new JsonParseException("Too many ingredients for recipe. The maximum is " + (ObfuscationReflectionHelper.findField(ShapedRecipe.class, "MAX_WIDTH").getInt(null) * ObfuscationReflectionHelper.findField(ShapedRecipe.class, "MAX_HEIGHT").getInt(null)));
            } else {
                return list;
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Serializer implements RecipeSerializer<ShapedToolRecipe> {

        public ShapedToolRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            String s = GsonHelper.getAsString(jsonObject, "group", "");
            CraftingBookCategory craftingBookCategory = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(jsonObject, "category", null), CraftingBookCategory.MISC);
            Map<String, Ingredient> map = ShapedRecipe.keyFromJson(GsonHelper.getAsJsonObject(jsonObject, "key"));
            String[] astring = ShapedRecipe.shrink(ShapedRecipe.patternFromJson(GsonHelper.getAsJsonArray(jsonObject, "pattern")));
            int i = astring[0].length();
            int j = astring.length;
            NonNullList<Ingredient> list = ShapedRecipe.dissolvePattern(astring, map, i, j);
            ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
            NonNullList<Ingredient> unconsumedItems = ingredientFromJson(GsonHelper.getAsJsonArray(jsonObject, "unconsumed"));
            return new ShapedToolRecipe(resourceLocation, s, craftingBookCategory, i, j, list, itemstack, unconsumedItems);
        }

        public ShapedToolRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            int i = friendlyByteBuf.readVarInt();
            int j = friendlyByteBuf.readVarInt();
            int size = friendlyByteBuf.readVarInt();
            String s = friendlyByteBuf.readUtf();
            CraftingBookCategory craftingBookCategory = friendlyByteBuf.readEnum(CraftingBookCategory.class);
            NonNullList<Ingredient> list = NonNullList.withSize(i * j, Ingredient.EMPTY);

            list.replaceAll(ignored -> Ingredient.fromNetwork(friendlyByteBuf));

            ItemStack itemStack = friendlyByteBuf.readItem();
            NonNullList<Ingredient> unconsumed = NonNullList.withSize(size, Ingredient.EMPTY);

            unconsumed.replaceAll(ignored -> Ingredient.fromNetwork(friendlyByteBuf));
            return new ShapedToolRecipe(resourceLocation, s, craftingBookCategory, i, j, list, itemStack, unconsumed);
        }

        public void toNetwork(FriendlyByteBuf friendlyByteBuf, ShapedToolRecipe recipe) {
            friendlyByteBuf.writeVarInt(recipe.width);
            friendlyByteBuf.writeVarInt(recipe.height);
            friendlyByteBuf.writeUtf(recipe.group);
            friendlyByteBuf.writeVarInt(recipe.unconsumedItems.size());

            for (Ingredient ingredient : recipe.recipeItems) {
                ingredient.toNetwork(friendlyByteBuf);
            }

            for (Ingredient unconsumedItem : recipe.unconsumedItems) {
                unconsumedItem.toNetwork(friendlyByteBuf);
            }

            friendlyByteBuf.writeItem(recipe.result);
        }
    }
}
