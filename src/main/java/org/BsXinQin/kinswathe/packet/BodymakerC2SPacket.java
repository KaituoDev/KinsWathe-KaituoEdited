package org.BsXinQin.kinswathe.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.BsXinQin.kinswathe.KinsWathe;


import java.util.UUID;

public record BodymakerC2SPacket(UUID target)implements CustomPayload {
    public static final Identifier BODYMAKER_PLAYLOAD_ID =Identifier.of(KinsWathe.MOD_ID,"bodymaker");
    public static final CustomPayload.Id<BodymakerC2SPacket> ID = new CustomPayload.Id<>(BODYMAKER_PLAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, BodymakerC2SPacket> CODEC;

    public BodymakerC2SPacket(UUID target){
        this.target = target;

    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.target);
    }

    public static BodymakerC2SPacket read(PacketByteBuf buf) {
        return new BodymakerC2SPacket(buf.readUuid());
    }

    public UUID target() {
        return this.target;
    }

    static {
        CODEC = PacketCodec.of(BodymakerC2SPacket::write, BodymakerC2SPacket::read);
    }


}