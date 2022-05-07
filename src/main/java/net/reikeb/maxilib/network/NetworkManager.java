package net.reikeb.maxilib.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.reikeb.maxilib.MaxiLib;
import net.reikeb.maxilib.network.packets.BiomeSingleUpdatePacket;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MaxiLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetworkManager {

    public static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(MaxiLib.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    @SuppressWarnings("UnusedAssignment")
    @SubscribeEvent
    public static void registerNetworkStuff(FMLCommonSetupEvent event) {
        int index = 0;
        INSTANCE.registerMessage(index++, BiomeSingleUpdatePacket.class, BiomeSingleUpdatePacket::encode, BiomeSingleUpdatePacket::decode, BiomeSingleUpdatePacket::whenThisPacketIsReceived);
    }
}