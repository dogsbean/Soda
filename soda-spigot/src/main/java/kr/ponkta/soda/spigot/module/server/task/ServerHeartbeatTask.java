package kr.ponkta.soda.spigot.module.server.task;

import kr.ponkta.soda.spigot.Soda;
import kr.ponkta.soda.spigot.module.database.redis.JedisModule;
import kr.ponkta.soda.spigot.module.server.Server;
import kr.ponkta.soda.spigot.module.server.ServerHandler;
import kr.ponkta.soda.packet.ServerHeartbeatPacket;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerHeartbeatTask extends BukkitRunnable {

    private final Soda plugin;

    private final JedisModule jedisModule;
    private final ServerHandler serverHandler;

    public ServerHeartbeatTask(Soda plugin) {
        this.plugin = plugin;

        this.jedisModule = plugin.getModuleHandler().getModule(JedisModule.class);
        this.serverHandler = plugin.getModuleHandler().getModule(ServerHandler.class);
    }

    @Override
    public void run() {
        Server server = this.serverHandler.getCurrentServer();
        server.update();
        this.jedisModule.sendPacket(new ServerHeartbeatPacket(server));
    }
}
