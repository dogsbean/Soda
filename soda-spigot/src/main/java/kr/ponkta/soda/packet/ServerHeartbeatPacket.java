package kr.ponkta.soda.packet;

import kr.ponkta.soda.spigot.Soda;
import kr.ponkta.soda.spigot.module.database.redis.packet.Packet;
import kr.ponkta.soda.spigot.module.server.Server;
import kr.ponkta.soda.spigot.module.server.ServerHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@AllArgsConstructor @Getter
public class ServerHeartbeatPacket extends Packet {

    private Server server;

    @Override
    public void onSend() {

    }

    @Override
    public void onReceive() {
        final ServerHandler serverHandler = JavaPlugin.getPlugin(Soda.class).getModuleHandler().getModule(ServerHandler.class);
        serverHandler.updateServer(server);
    }
}
