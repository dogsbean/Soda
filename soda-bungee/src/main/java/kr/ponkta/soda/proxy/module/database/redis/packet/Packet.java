package kr.ponkta.soda.proxy.module.database.redis.packet;

public abstract class Packet {

    public abstract void onReceive();
    public abstract void onSend();

}