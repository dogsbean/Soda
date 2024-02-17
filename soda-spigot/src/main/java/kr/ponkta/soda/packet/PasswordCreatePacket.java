package kr.ponkta.soda.packet;

import kr.ponkta.soda.spigot.Soda;
import kr.ponkta.soda.spigot.module.database.redis.packet.Packet;
import kr.ponkta.soda.spigot.module.profile.Profile;
import kr.ponkta.soda.spigot.module.profile.ProfileHandler;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class PasswordCreatePacket extends Packet {

    private final String uuid;
    private final String password;

    @Override
    public void onReceive() {
        final ProfileHandler profileHandler = Soda.getInstance().getModuleHandler().getModule(ProfileHandler.class);
        final Optional<Profile> profileOptional = profileHandler.getProfile(UUID.fromString(uuid));
        if(!profileOptional.isPresent()) return;
        final Profile profile = profileOptional.get();
        profile.setPassword(password);
        profileHandler.sendSync(profile);
    }

    @Override
    public void onSend() {

    }
}
