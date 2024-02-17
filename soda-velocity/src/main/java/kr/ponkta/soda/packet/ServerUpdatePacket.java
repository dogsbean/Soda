package kr.ponkta.soda.packet;

import kr.ponkta.soda.proxy.module.database.redis.packet.Packet;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ServerUpdatePacket extends Packet {

    private String serverName;

    @Override
    public void onReceive() {

    }

    @Override
    public void onSend() {

    }
}
