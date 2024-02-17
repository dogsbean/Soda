package kr.ponkta.soda.packet;

import kr.ponkta.soda.spigot.Soda;
import kr.ponkta.soda.spigot.module.database.redis.packet.Packet;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;

@AllArgsConstructor
public class ConsoleCommandPacket extends Packet {

    private final String command;

    @Override
    public void onReceive() {
        Bukkit.getScheduler().runTask(Soda.getInstance(), () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command + " -c");
        });
    }

    @Override
    public void onSend() {

    }
}
