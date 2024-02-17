package kr.ponkta.soda.packet;

import kr.ponkta.soda.packet.type.RefreshType;
import kr.ponkta.soda.proxy.PrimeProxy;
import kr.ponkta.soda.proxy.module.database.redis.packet.Packet;
import kr.ponkta.soda.proxy.module.rank.Rank;
import kr.ponkta.soda.proxy.module.rank.RankHandler;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RankRefreshPacket extends Packet {

    private Rank rank;
    private RefreshType type;

    @Override
    public void onSend() {
    }

    @Override
    public void onReceive() {
        final RankHandler rankHandler = PrimeProxy.getInstance().getModuleHandler().getModule(RankHandler.class);
        switch(type) {
            case UPDATE: {
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