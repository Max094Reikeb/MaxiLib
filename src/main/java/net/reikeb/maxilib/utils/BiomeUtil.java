package net.reikeb.maxilib.utils;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraftforge.network.PacketDistributor;
import net.reikeb.maxilib.MaxiLib;
import net.reikeb.maxilib.network.NetworkManager;
import net.reikeb.maxilib.network.packets.BiomeSingleUpdatePacket;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class BiomeUtil {

    private static final int WIDTH_BITS = (int) Math.round(Math.log(16.0D) / Math.log(2.0D)) - 2;
    private static final int HEIGHT_BITS = (int) Math.round(Math.log(256.0D) / Math.log(2.0D)) - 2;
    private static final int VERTICAL_MASK = (1 << HEIGHT_BITS) - 1;
    private static final int HORIZONTAL_MASK = (1 << WIDTH_BITS) - 1;

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
        NetworkManager.INSTANCE.send(PacketDistributor.ALL.noArg(), new BiomeSingleUpdatePacket(pos, resourceLocation));
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
        NetworkManager.INSTANCE.send(PacketDistributor.ALL.noArg(), new BiomeSingleUpdatePacket(pos, biome.getRegistryName()));
    }

    /**
     * Modifies the biome at a location by another biome
     *
     * @param level The level of the biome
     * @param pos   The location of the biome
     * @param biome The other biome to replace with
     */
    public static void setBiomeAtPos(Level level, BlockPos pos, Biome biome) {
        ChunkAccess chunkAccess = level.getChunk(pos);
        int biomeIndex = getBiomeIndex(pos);
        if (biomeIndex < chunkAccess.getSections().length) {
            LevelChunkSection chunkSection = chunkAccess.getSection(biomeIndex);
            PalettedContainer<Holder<Biome>> biomeContainer = chunkSection.getBiomes();
            biomeContainer.acquire();

            try {
                for (int j = 0; j < 4; ++j) {
                    for (int k = 0; k < 4; k++) {
                        for (int l = 0; l < 4; ++l) {
                            biomeContainer.getAndSetUnchecked(j, k, l, Holder.direct(biome));
                        }
                    }
                }
            } finally {
                biomeContainer.release();
            }
        } else {
            MaxiLib.LOGGER.error(String.format("Failed to replace biome at pos: %s; by biome: %s", pos, biome));
        }
        chunkAccess.setUnsaved(true);
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

    /**
     * Gets the number index of a biome
     *
     * @param blockPos position of the biome
     * @return biome index
     */
    private static int getBiomeIndex(BlockPos blockPos) {
        int i = blockPos.getX() - 2;
        int j = blockPos.getY() - 2;
        int k = blockPos.getZ() - 2;
        int l = i >> 2;
        int i1 = j >> 2;
        int j1 = k >> 2;
        double d0 = (double) (i & 3) / 4.0D;
        double d1 = (double) (j & 3) / 4.0D;
        double d2 = (double) (k & 3) / 4.0D;
        double[] adouble = new double[8];

        for (int k1 = 0; k1 < 8; ++k1) {
            boolean flag = (k1 & 4) == 0;
            boolean flag1 = (k1 & 2) == 0;
            boolean flag2 = (k1 & 1) == 0;
            int l1 = flag ? l : l + 1;
            int i2 = flag1 ? i1 : i1 + 1;
            int j2 = flag2 ? j1 : j1 + 1;
            double d3 = flag ? d0 : d0 - 1.0D;
            double d4 = flag1 ? d1 : d1 - 1.0D;
            double d5 = flag2 ? d2 : d2 - 1.0D;
            adouble[k1] = maxilib$func_biome_a(0, l1, i2, j2, d3, d4, d5);
        }

        int k2 = 0;
        double d6 = adouble[0];

        for (int l2 = 1; l2 < 8; ++l2) {
            if (d6 > adouble[l2]) {
                k2 = l2;
                d6 = adouble[l2];
            }
        }

        int i3 = (k2 & 4) == 0 ? l : l + 1;
        int j3 = (k2 & 2) == 0 ? i1 : i1 + 1;
        int k3 = (k2 & 1) == 0 ? j1 : j1 + 1;

        int arrayIndex = i3 & HORIZONTAL_MASK;
        arrayIndex |= (k3 & HORIZONTAL_MASK) << WIDTH_BITS;
        return arrayIndex | Mth.clamp(j3, 0, VERTICAL_MASK) << WIDTH_BITS + WIDTH_BITS;
    }

    private static double maxilib$func_biome_a(long p_226845_0_, int p_226845_2_, int p_226845_3_, int p_226845_4_, double p_226845_5_, double p_226845_7_, double p_226845_9_) {
        long linCongGen = LinearCongruentialGenerator.next(p_226845_0_, p_226845_2_);
        linCongGen = LinearCongruentialGenerator.next(linCongGen, p_226845_3_);
        linCongGen = LinearCongruentialGenerator.next(linCongGen, p_226845_4_);
        linCongGen = LinearCongruentialGenerator.next(linCongGen, p_226845_2_);
        linCongGen = LinearCongruentialGenerator.next(linCongGen, p_226845_3_);
        linCongGen = LinearCongruentialGenerator.next(linCongGen, p_226845_4_);
        double d0 = maxilib$func_biome_b(linCongGen);
        linCongGen = LinearCongruentialGenerator.next(linCongGen, p_226845_0_);
        double d1 = maxilib$func_biome_b(linCongGen);
        linCongGen = LinearCongruentialGenerator.next(linCongGen, p_226845_0_);
        double d2 = maxilib$func_biome_b(linCongGen);
        return square(p_226845_9_ + d2) + square(p_226845_7_ + d1) + square(p_226845_5_ + d0);
    }

    private static double maxilib$func_biome_b(long p_226844_0_) {
        double d0 = (double) ((int) Math.floorMod(p_226844_0_ >> 24, 1024L)) / 1024.0D;
        return (d0 - 0.5D) * 0.9D;
    }

    /**
     * Calculate the square of a number (n*n)
     *
     * @param n the number
     * @return the square
     */
    private static double square(double n) {
        return n * n;
    }
}