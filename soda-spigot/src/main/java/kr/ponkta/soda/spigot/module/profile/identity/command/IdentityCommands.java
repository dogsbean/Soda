package kr.ponkta.soda.spigot.module.profile.identity.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import kr.ponkta.soda.spigot.Soda;
import kr.ponkta.soda.spigot.module.ModuleHandler;
import kr.ponkta.soda.spigot.module.profile.Profile;
import kr.ponkta.soda.spigot.module.profile.ProfileHandler;
import kr.ponkta.soda.spigot.module.profile.target.ProfileTarget;
import kr.ponkta.soda.spigot.util.Color;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IdentityCommands {

    private static final Soda plugin = Soda.getInstance();
    private static final ModuleHandler moduleHandler = plugin.getModuleHandler();

    private static final ProfileHandler profileHandler = moduleHandler.getModule(ProfileHandler.class);

    @Command(names = {"alts"}, description = "View a player's alts", async = true, permission = "Soda.command.alts")
    public static void executeAlts(CommandSender sender, @Parameter(name = "player")ProfileTarget profileTarget) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            final List<Profile> allProfiles = new ArrayList<>();
            profile.getIdentities().forEach(identity -> allProfiles.addAll(profileHandler.fetchByIdentity(identity.getIp())));
            final List<Profile> associatedProfiles = allProfiles.stream().distinct().collect(Collectors.toList());
            sender.sendMessage(Color.translate(profile.getColoredName() + " &ahas &e" + associatedProfiles.size() + " &aaccounts on &e" + profile.getIdentities().size() + " &aidentities."));
            if(sender.hasPermission("Soda.alts.detailed")) sender.sendMessage(Color.translate("&eLast joined with &7" + profile.getLastHashedIp()));
            sender.sendMessage(associatedProfiles
                    .stream()
                    .map(prof -> Color.translate(prof.getColoredName() + (sender.hasPermission("Soda.alts.detailed") ? " &7❘ &c(" + prof.getLastHashedIp() + ") &f" + String.join(", ", prof.getAllHashedIps()) : "")))
                    .collect(Collectors.joining("\n")));
        });
    }
}