package kr.ponkta.soda.spigot.module.database.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kr.ponkta.soda.spigot.module.Module;
import kr.ponkta.soda.spigot.module.database.redis.packet.Packet;
import kr.ponkta.soda.spigot.util.json.JsonHelper;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Getter
public class JedisModule extends Module {

    private String host;
    private String password;
    private int port;
    private boolean auth;

    private JedisPool jedisPool;

    private String channel;

    private Gson gson;

    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Override
    public void onEnable() {
        this.host = getPlugin().getConfig().getString("redis.host");
        this.port = getPlugin().getConfig().getInt("redis.port");
        this.channel = getPlugin().getConfig().getString("redis.channel");
        this.auth = getPlugin().getConfig().getBoolean("redis.auth");
        this.password = getPlugin().getConfig().getString("redis.password");

        this.gson = JsonHelper.GSON;
        connect();
    }

    /**
     * Attempts to make a connection to the
     * redis database with the specified credentials and
     * starts a thread for receiving messages
     */
    public void connect() {
        this.jedisPool = new JedisPool(host, port);
        if(this.auth) {
            this.jedisPool.getResource().auth(this.password);
        }

        new Thread(() -> this.runCommand(redis -> {
            if(this.auth) {
                redis.auth(this.password);
            }
            redis.subscribe(new JedisPubSub() {

                @Override
                public void onMessage(String channel, String message) {
                    try {
                        // Create the packet
                        String[] strings = message.split("/split/");
                        Object jsonObject = gson.fromJson(strings[1], Class.forName(strings[0]));
                        if(jsonObject == null) return;

                        Packet packet = (Packet) jsonObject;

                        packet.onReceive();

                    } catch (Exception ex) {
                        // do nothing
                    }
                }
            }, channel);
        })).start();

    }

    /**
     * sends a packet through redis
     *
     * @Param packet the packet to get sent
     */

    public void sendPacket(Packet packet) {
        packet.onSend();

        executorService.execute(() -> runCommand(redis -> {
            if(this.auth) {
                redis.auth(this.password);
            }

            redis.publish(channel, packet.getClass().getName() + "/split/" + gson.toJson(packet));
        }));
    }

    /**
     * sends a packet through redis
     *
     * @Param consumer the callback to be executed
     */
    public void runCommand(Consumer<Jedis> consumer) {
        Jedis jedis = jedisPool.getResource();
        if (jedis != null) {
            if(this.auth) {
                jedis.auth(this.password);
            }
            consumer.accept(jedis);
            jedisPool.returnResource(jedis);
        }
    }
}
