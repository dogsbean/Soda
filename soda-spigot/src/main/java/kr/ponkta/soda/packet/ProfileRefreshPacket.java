package kr.ponkta.soda.packet;

import kr.ponkta.soda.packet.type.RefreshType;
import kr.ponkta.soda.spigot.Soda;
import kr.ponkta.soda.spigot.module.database.redis.packet.Packet;
import kr.ponkta.soda.spigot.module.profile.Profile;
import kr.ponkta.soda.spigot.module.profile.ProfileHandler;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@AllArgsConstructor
public class ProfileRefreshPacket extends Packet {

    private Profile profile;
    private RefreshType type;

    @Override
    public void onSend() {
    }

    @Override
    public void onReceive() {
        final ProfileHandler profileHandler = Soda.getInstance().getModuleHandler().getModule(ProfileHandler.class);
        switch(type) {
            case UPDATE: {
                profileHandler.updateProfile(profile);
                break;
            }
            case REMOVE: {
                profileHandler.getProfiles().remove(profile);
                break;
            }
        }
    }
}
