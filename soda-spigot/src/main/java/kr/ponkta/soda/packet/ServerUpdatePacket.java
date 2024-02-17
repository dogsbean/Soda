package kr.ponkta.soda.packet;

import kr.ponkta.soda.spigot.Soda;
import kr.ponkta.soda.spigot.module.database.redis.packet.Packet;
import kr.ponkta.soda.spigot.module.server.ServerHandler;
import kr.ponkta.soda.spigot.module.server.filter.ChatFilterHandler;
import lombok.AllArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

@AllArgsConstructor
public class ServerUpdatePacket extends Packet {

    private String serverName;

    @Override
    public void onReceive() {
        final Soda plugin = JavaPlugin.getPlugin(Soda.class);
        final ServerHandler serverHandler = plugin.getModuleHandler().getModule(ServerHandler.class);

        if(this.serverName.equalsIgnoreCase(serverHandler.getCurrentName())) {
            plugin.reloadConfig();
            plugin.getModuleHandler().getModule(ChatFilterHandler.class).loadFilters();
        }
    }

    @Override
    public void onSend() {

    }
}
