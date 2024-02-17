package kr.ponkta.soda.spigot.module.server.filter;

import kr.ponkta.soda.packet.ServerUpdatePacket;
import kr.ponkta.soda.spigot.module.Module;
import kr.ponkta.soda.spigot.module.database.redis.JedisModule;
import kr.ponkta.soda.spigot.module.server.ServerHandler;
import kr.ponkta.soda.spigot.util.Color;
import org.bukkit.ChatColor;

import java.util.*;

public class ChatFilterHandler extends Module {

    private JedisModule jedisModule;

    private Map<UUID, ChatFilter> filters;

    @Override
    public void onEnable() {
        super.onEnable();

        this.jedisModule = getModuleHandler().getModule(JedisModule.class);
        loadFilters();
    }

    public Collection<ChatFilter> getFilters() {
        return filters.values();
    }

    public void trackFilter(ChatFilter filter) {
        filters.put(filter.getId(), filter);
    }

    public void forgetFilter(ChatFilter filter) {
        filters.remove(filter.getId());
    }

    public void saveFilter(ChatFilter filter) {
        jedisModule.runCommand(jedis -> {
            jedis.sadd("Prime:ChatFilters.Filters", filter.getId().toString());
            jedis.hmset("Prime:ChatFilters.Filter." + filter.getId(), filter.toMap());
        });

        sendUpdate();
    }

    public void deleteFilter(ChatFilter filter) {
        forgetFilter(filter);

        jedisModule.runCommand(jedis -> {
            jedis.srem("Prime:ChatFilters.Filters", filter.getId().toString());
            jedis.del("Prime:ChatFilters:Filter");
        });

        sendUpdate();
    }

    public void loadFilters() {
        jedisModule.runCommand(jedis -> {
            final Map<UUID, ChatFilter> map = new HashMap<>();

            for(String filterId : jedis.smembers("Prime:ChatFilters.Filters")) {
                if(jedis.exists("Prime:ChatFilters.Filter." + filterId)) {
                    final ChatFilter filter = new ChatFilter(
                            jedis.hgetAll("Prime:ChatFilters.Filter." + filterId));
                    map.put(filter.getId(), filter);
                }
            }

            filters = map;
        });
    }

    public ChatFilter filterMessage(String message) {
        final String newMessage = ChatColor.stripColor(Color.translate(message)).toLowerCase();
        for(ChatFilter filter : getFilters()) {
            if(filter.getPattern().matcher(newMessage).find()) {
                return filter;
            }
        }

        return null;
    }

    private void sendUpdate() {
        jedisModule.sendPacket(new ServerUpdatePacket(getModuleHandler().getModule(ServerHandler.class).getCurrentName()));
    }

}
