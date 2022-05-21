package net.reikeb.maxilib.utils;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.reikeb.maxilib.MaxiLib;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class BiomeUtil {

    /**
     * Modifies the biome at a location by a biome's ResourceLocation
     *
     * @param level            The level of the biome
     * @param pos              The location of the biome
     * @param resourceLocation The biome's ResourceLocation to replace with
     */
    public static void setBiomeAtPos(Level level, BlockPos pos, ResourceLocation resourceLocation) {
        Biome biome = level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).get(resourceLocation);
        if (biome == null) return;
        if (level.isClientSide) return;
        setBiomeAtPos(level, pos, biome);
        // NetworkManager.INSTANCE.send(PacketDistributor.ALL.noArg(), new BiomeSingleUpdatePacket(pos, resourceLocation));
    }

    /**
     * Modifies the biome at a location by a biome's ResourceKey
     *
     * @param level    The level of the biome
     * @param pos      The location of the biome
     * @param biomeKey The biome's ResourceKey to replace with
     */
    public static void setBiomeKeyAtPos(Level level, BlockPos pos, ResourceKey<Biome> biomeKey) {
        Biome biome = level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).get(biomeKey);
        if (biome == null) return;
        if (level.isClientSide) return;
        setBiomeAtPos(level, pos, biome);
        // NetworkManager.INSTANCE.send(PacketDistributor.ALL.noArg(), new BiomeSingleUpdatePacket(pos, biome.getRegistryName()));
    }

    /**
     * Modifies the biome at a location by another biome
     *
     * @param level The level of the biome
     * @param pos   The location of the biome
     * @param biome The other biome to replace with
     */
    public static void setBiomeAtPos(Level level, BlockPos pos, Biome biome) {
        LevelChunk chunk = level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
        PalettedContainer<Holder<Biome>> biomes = chunk.getSection(chunk.getSectionIndex(pos.getY())).getBiomes();
        biomes.getAndSetUnchecked(
                pos.getX() & 3, pos.getY() & 3, pos.getZ() & 3,
                Holder.direct(biome)
        );
        chunk.setUnsaved(true);
    }

    /**
     * Method that finds nearest biome from a position.
     * WARNING : ONLY RUN THE METHOD SERVER-SIDE!
     *
     * @param serverLevel   World where to check for the biome
     * @param startPosition The position from where we start to search
     * @param biomeToFind   The biome we want to find
     * @return Closest position of the biome
     */
    @Nullable // This runs server side ONLY, so `ServerLevel` is safe here.
    public static BlockPos getNearestBiomePosition(ServerLevel serverLevel, BlockPos startPosition, ResourceKey<Biome> biomeToFind) {
        return getNearestBiomePosition(serverLevel, startPosition, biomeToFind, 6400, 8);
    }

    /**
     * Method that finds nearest biome from a position.
     * WARNING : ONLY RUN THE METHOD SERVER-SIDE!
     *
     * @param serverLevel    World where to check for the biome
     * @param startPosition  The position from where we start to search
     * @param biomeToFind    The biome we want to find
     * @param searchRadius   The radius from where we search the biome
     * @param incrementation Each time the search loops, this increases the xz position from the origin, think of it as a "precision", this will check every 8 blocks, if this is "8", until the search radius is reached.
     * @return Closest position of the biome
     */
    @Nullable // This runs server side ONLY, so `ServerLevel` is safe here.
    public static BlockPos getNearestBiomePosition(ServerLevel serverLevel, BlockPos startPosition, ResourceKey<Biome> biomeToFind, int searchRadius, int incrementation) {
        Registry<Biome> biomes = serverLevel.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        // The holder for biome resource key
        Holder<Biome> holderToFind = biomes.getHolder(biomeToFind).orElseThrow(() -> new IllegalArgumentException("This biome does not exist in the biome registry!"));

        // Biome doesn't exist in this biome source, therefore we don't want to search (which causes INSANE server side lag) and return null
        if (!serverLevel.getChunkSource().getGenerator().getBiomeSource().possibleBiomes().contains(holderToFind)) {
            MaxiLib.LOGGER.error(new TranslatableComponent("commands.locatebiome.notFound"));
            return null;
        }

        // We want to check if the holder found in the search matches our biome's holder, effectively the filter that determines when the nearest biome is found.
        Predicate<Holder<Biome>> holderToFindPredicate = biomeHolder -> biomeHolder == holderToFind;

        Pair<BlockPos, Holder<Biome>> nearestBiome = serverLevel.findNearestBiome(holderToFindPredicate, startPosition, searchRadius, incrementation);
        // If this returns null, no biome was found.
        if (nearestBiome == null) {
            MaxiLib.LOGGER.error(new TranslatableComponent("commands.locatebiome.notFound"));
            return null;
        }
        // Position of the nearest biome.
        return nearestBiome.getFirst();
    }
}