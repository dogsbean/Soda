package kr.ponkta.soda.packet;

import kr.ponkta.soda.proxy.PrimeProxy;
import kr.ponkta.soda.proxy.module.database.redis.packet.Packet;
import kr.ponkta.soda.proxy.module.server.Server;
import kr.ponkta.soda.proxy.module.server.ServerHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ServerHeartbeatPacket extends Packet {

    private Server server;

    @Override
    public void onSend() {

    }

    @Override
    public void onReceive() {
        final ServerHandler serverHandler = PrimeProxy.getInstance().getModuleHandler().getModule(ServerHandler.class);
        serverHandler.updateServer(server);
    }
}
