package kr.ponkta.soda.spigot.module.server.scoreboard;

import com.elevatemc.elib.util.Pair;
import kr.ponkta.soda.spigot.Soda;
import kr.ponkta.soda.spigot.module.profile.Profile;
import kr.ponkta.soda.spigot.module.profile.ProfileHandler;
import kr.ponkta.soda.spigot.module.server.ServerHandler;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;

@UtilityClass
public class PrimeScoreboardStyle {

    private final ProfileHandler profileHandler = Soda.getInstance().getModuleHandler().getModule(ProfileHandler.class);
    private final ServerHandler serverHandler = Soda.getInstance().getModuleHandler().getModule(ServerHandler.class);

    public Pair<ChatColor, ChatColor> getStyle(Player player) {
        final Optional<Profile> profileOptional = profileHandler.getProfile(player.getUniqueId());
        if(!profileOptional.isPresent()) return DEFAULT_STYLE;
        final Profile profile = profileOptional.get();
        if(!profile.hasStyle()) return DEFAULT_STYLE;

        final Pair<ChatColor, ChatColor> activeStyle = serverHandler.getStyles().get(StringUtils.capitalize(profile.getStyle().toLowerCase()));
        return activeStyle == null ? DEFAULT_STYLE : activeStyle;
    }

    public Pair<ChatColor, ChatColor> DEFAULT_STYLE = new Pair<>(ChatColor.DARK_AQUA, ChatColor.WHITE);
}
