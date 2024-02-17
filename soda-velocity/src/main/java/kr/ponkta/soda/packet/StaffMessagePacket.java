package kr.ponkta.soda.packet;

import kr.ponkta.soda.proxy.module.database.redis.packet.Packet;
import kr.ponkta.soda.packet.type.StaffMessageType;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class StaffMessagePacket extends Packet {

    private StaffMessageType type;
    private UUID uuid;
    private String prevServer, server;

    @Override
    public void onReceive() {

    }

    @Override
    public void onSend() {
    }
}
