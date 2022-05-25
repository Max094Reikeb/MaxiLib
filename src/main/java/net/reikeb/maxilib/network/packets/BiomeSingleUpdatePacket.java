package net.reikeb.maxilib.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.reikeb.maxilib.utils.BiomeUtil;

import java.util.function.Supplier;

public record BiomeSingleUpdatePacket(BlockPos pos, ResourceLocation biomeLocation) {

    public static BiomeSingleUpdatePacket decode(FriendlyByteBuf buf) {
        return new BiomeSingleUpdatePacket(buf.readBlockPos(), buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeResourceLocation(biomeLocation);
    }

    public void whenThisPacketIsReceived(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> new BiomeUpdate(pos, biomeLocation));
        });
        context.get().setPacketHandled(true);
    }

    public record BiomeUpdate(BlockPos pos, ResourceLocation biomeLocation) implements DistExecutor.SafeCallable<Object> {

        @Override
        public Object call() {
            ClientLevel clientLevel = Minecraft.getInstance().level;
            if (clientLevel == null) return null;
            BiomeUtil.setBiomeAtPos(clientLevel, pos, BiomeUtil.getBiome(clientLevel, biomeLocation, Registry::get));
            return null;
        }
    }
}
