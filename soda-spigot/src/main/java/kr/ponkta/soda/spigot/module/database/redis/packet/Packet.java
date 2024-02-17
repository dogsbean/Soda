package kr.ponkta.soda.spigot.module.database.redis.packet;

public abstract class Packet {

    public abstract void onReceive();
    public abstract void onSend();

}