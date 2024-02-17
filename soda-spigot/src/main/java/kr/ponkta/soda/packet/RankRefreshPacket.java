package kr.ponkta.soda.packet;

import kr.ponkta.soda.packet.type.RefreshType;
import kr.ponkta.soda.spigot.Soda;
import kr.ponkta.soda.spigot.module.database.redis.packet.Packet;
import kr.ponkta.soda.spigot.module.rank.Rank;
import kr.ponkta.soda.spigot.module.rank.RankHandler;
import kr.ponkta.soda.spigot.util.Color;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.java.JavaPlugin;

@AllArgsConstructor
public class RankRefreshPacket extends Packet {

    private Rank rank;
    private RefreshType type;

    @Override
    public void onSend() {
    }

    @Override
    public void onReceive() {
        final RankHandler rankHandler = JavaPlugin.getPlugin(Soda.class).getModuleHandler().getModule(RankHandler.class);
        switch(type) {
            case UPDATE: {
                Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(player -> {
                    player.sendMessage(Color.translate("&9[Monitor] &f" + rank.getColoredDisplay() + " &f -> Updated"));
                });
                rankHandler.updateRank(rank);
                break;
            }
            case REMOVE: {
                rankHandler.getCache().remove(rank);
                break;
            }
        }
    }
}