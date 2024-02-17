package kr.ponkta.soda.packet;

import kr.ponkta.soda.packet.type.RefreshType;
import kr.ponkta.soda.proxy.PrimeProxy;
import kr.ponkta.soda.proxy.module.database.redis.packet.Packet;
import kr.ponkta.soda.proxy.module.profile.Profile;
import kr.ponkta.soda.proxy.module.profile.ProfileHandler;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProfileRefreshPacket extends Packet {

    private Profile profile;
    private RefreshType type;

    @Override
    public void onSend() {
    }

    @Override
    public void onReceive() {
        final ProfileHandler profileHandler = PrimeProxy.getInstance().getModuleHandler().getModule(ProfileHandler.class);
        switch(type) {
            case UPDATE: {
                profileHandler.updateProfile(profile);
                break;
            }
            case REMOVE: {
                // do nothing Lol
                break;
            }
        }
    }
}
