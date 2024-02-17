package kr.ponkta.soda.spigot.module.profile.target;

import com.elevatemc.elib.util.Callback;
import kr.ponkta.soda.spigot.Soda;
import kr.ponkta.soda.spigot.module.profile.Profile;
import kr.ponkta.soda.spigot.module.profile.ProfileHandler;
import kr.ponkta.soda.spigot.util.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ProfileTarget {

    private static final ProfileHandler profileHandler = Soda.getInstance().getModuleHandler().getModule(ProfileHandler.class);

    private final String name;
    private UUID knownUUID;

    public ProfileTarget(String name) {
        this.name = name;
    }

    public ProfileTarget(CommandSender sender) {
        if(sender instanceof Player) {
            final Player player = (Player) sender;

            this.name = player.getName();
            this.knownUUID = player.getUniqueId();
        } else {
            this.name = "self";
        }
    }

    public void resolve(Callback<Profile> callback) {
        if(knownUUID != null) {
            callback.callback(profileHandler.getProfile(knownUUID).orElse(null));
            return;
        }

        profileHandler.getProfile(name).thenAccept(callback::callback).exceptionally(throwable -> {
            callback.callback(null);
            return null;
        });
    }

    public void sendError(CommandSender sender) {
        sender.sendMessage(Color.translate(String.format("&cFailed to fetch %s's profile", name)));
    }
}
