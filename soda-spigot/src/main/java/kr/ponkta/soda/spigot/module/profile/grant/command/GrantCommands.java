package kr.ponkta.soda.spigot.module.profile.grant.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.elib.util.UUIDUtils;
import kr.ponkta.soda.spigot.Soda;
import kr.ponkta.soda.spigot.PrimeConstants;
import kr.ponkta.soda.spigot.module.ModuleHandler;
import kr.ponkta.soda.spigot.module.profile.Profile;
import kr.ponkta.soda.spigot.module.profile.ProfileHandler;
import kr.ponkta.soda.spigot.module.profile.grant.Grant;
import kr.ponkta.soda.spigot.module.profile.grant.menu.GrantMenu;
import kr.ponkta.soda.spigot.module.profile.grant.menu.GrantsMenu;
import kr.ponkta.soda.spigot.module.profile.target.ProfileTarget;
import kr.ponkta.soda.spigot.module.rank.Rank;
import kr.ponkta.soda.spigot.module.rank.RankHandler;
import kr.ponkta.soda.spigot.module.webhook.DiscordWebhook;
import kr.ponkta.soda.spigot.util.Color;
import kr.ponkta.soda.spigot.util.time.DurationUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class GrantCommands {

    private static final Soda plugin = Soda.getInstance();
    private static final ModuleHandler moduleHandler = plugin.getModuleHandler();
    
    private static final ProfileHandler profileHandler = moduleHandler.getModule(ProfileHandler.class);
    private static final RankHandler rankHandler = moduleHandler.getModule(RankHandler.class);

    private static final String GRANT_WEBHOOK = "https://discord.com/api/webhooks/993224084060115004/Rpd7FcojkkgLlbWJQg938NkwlarCA0Kuw_9uNfxyVEMMwBzlbaxkNnA7gdPkHKcHm6P3";
    
    @Command(names = {"grant"}, permission = "Soda.command.grant", description = "Grant a player a rank")
    public static void executeGrant(Player player, @Parameter(name = "player") ProfileTarget profileTarget) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(player);
                return;
            }

            Soda.getInstance().getServer().getScheduler().runTask(Soda.getInstance(), () -> {
                new GrantMenu(profile).openMenu(player);
            });
        });
    }

    @Command(names = {"ogrant"}, async = true, permission = "Soda.command.grant", description = "Grant a player a rank")
    public static void executeOGrant(CommandSender sender, @Parameter(name = "player") ProfileTarget profileTarget, @Parameter(name = "rank") Rank rank, @Parameter(name = "duration") String durationString, @Parameter(name = "scopes") String scopesString, @Parameter(name = "reason", wildcard = true) String reason) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            if(!(sender.hasPermission("Soda.grant.create." + rank.getName()))) {
                sender.sendMessage(Color.translate("&cYou do not have permission to create a grant for this rank."));
                return;
            }

            long duration = DurationUtils.fromString(durationString);

            UUID addedBy;

            if(sender instanceof Player) {
                Player player = (Player) sender;
                addedBy = player.getUniqueId();
            } else {
                addedBy = PrimeConstants.CONSOLE_UUID;
            }

            List<String> scopes = Arrays.asList(scopesString.split(",").clone());

            Grant grant = new Grant(
                    rank,
                    addedBy,
                    System.currentTimeMillis(),
                    reason,
                    duration,
                    scopes
            );
            profile.getGrants().add(grant);

            profileHandler.save(profile);

            sender.sendMessage(Color.translate("&aYou have granted &r" + profile.getColoredName() + " &athe &r" + rank.getColoredDisplay() + " &arank for &f" + (duration == Long.MAX_VALUE ? "forever" : DurationUtils.toString(System.currentTimeMillis() + duration)) + "&a."));

            Player targetPlayer = Bukkit.getPlayer(profile.getUuid());
            if(targetPlayer != null && targetPlayer.isOnline()) {
                targetPlayer.sendMessage(Color.translate("&aYou have been granted &r" + rank.getColoredDisplay()+ " &afor &f" + (duration == Long.MAX_VALUE ? "forever" : DurationUtils.toString(System.currentTimeMillis() + duration)) + "&a."));
            }

            final DiscordWebhook webhook = new DiscordWebhook(GRANT_WEBHOOK);
            webhook.addEmbed(
                    new DiscordWebhook.EmbedObject()
                            .setTitle(profile.getUsername() + " has been granted " + rank.getName())
                            .addField("Added By", (grant.getAddedBy().equals(PrimeConstants.CONSOLE_UUID) ? "Console" : UUIDUtils.name(grant.getAddedBy())), false)
                            .addField("Reason", grant.getAddedReason(), false)
                            .addField("Duration", grant.formatDuration(), false)
                            .addField("Scopes", String.join(", ", grant.getScopes()), false)
                            .setColor(java.awt.Color.cyan)
                            .setFooter("Prime Grants", null)
            );
            new Thread(() -> {
                try {
                    webhook.execute();
                }catch(Exception ex) {
                    ex.printStackTrace();
                }
            }, "grant-log-" + addedBy).start();
        });
    }

    @Command(names = {"ipbypass add", "sibling add"}, description = "Allow a player to bypass alt ip checks", permission = "Soda.command.ipbypass")
    public static void sibling(CommandSender sender, @Parameter(name = "player") ProfileTarget profileTarget, @Parameter(name = "reason", wildcard = true, defaultValue = "No reason provided") String reason) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            UUID addedBy;
            if(sender instanceof Player) {
                Player player = (Player) sender;
                addedBy = player.getUniqueId();
            } else {
                addedBy = PrimeConstants.CONSOLE_UUID;
            }

            final Rank bypass = rankHandler.getRank("IP Bypass").orElse(null);
            if(bypass == null) {
                sender.sendMessage(Color.translate("&cCould not fetch the bypass rank."));
                return;
            }

            Grant grant = new Grant(
                    bypass,
                    addedBy,
                    System.currentTimeMillis(),
                    reason,
                    Long.MAX_VALUE,
                    Collections.singletonList("Global")
            );
            profile.getGrants().add(grant);

            profileHandler.save(profile);
            sender.sendMessage(Color.translate("&aYou have granted &r" + profile.getColoredName() + " &aip bypass."));
        });
    }

    @Command(names = {"grants"}, async = true, description = "Check a player's grants", permission = "Soda.command.grants")
    public static void executeGrants(Player player, @Parameter(name = "player") ProfileTarget profileTarget) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(player);
                return;
            }

            Soda.getInstance().getServer().getScheduler().runTask(Soda.getInstance(), () -> {
                new GrantsMenu(profile, new ArrayList<>(profile.getGrants())).openMenu(player);
            });
        });
    }

    @Command(names = {"granthistory"}, async = true, description = "Check a player's grant history", permission = "Soda.command.granthistory")
    public static void executeGrantHistory(Player player, @Parameter(name = "player") ProfileTarget profileTarget) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(player);
                return;
            }

            player.sendMessage(Color.translate("&aSearching the database for all grants made by " + profile.getUuid() + ". This may take a few moments."));
            final List<Grant> grants = profileHandler.getGrantsByStaff(profile.getUuid());
            if(grants == null) {
                player.sendMessage(Color.translate("&cFailed to fetch grants."));
                return;
            }
            new GrantsMenu(profile, grants)
                    .showWhoReceived()
                    .openMenu(player);
        });
    }

    @Command(names = {"granthistoryundo", "grantrollback"}, async = true, description = "Rollback a staff member's grants", permission = "Soda.command.granthistoryundo")
    public static void executeGrantHistoryUndo(CommandSender sender, @Parameter(name = "player") ProfileTarget profileTarget, @Parameter(name = "duration") String durationString, @Parameter(name = "rollback reason") String reason) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            long duration = DurationUtils.fromString(durationString);

            AtomicReference<UUID> removedBy = new AtomicReference<>(PrimeConstants.CONSOLE_UUID);
            if(sender instanceof Player) {
                Player player = (Player) sender;
                removedBy.set(player.getUniqueId());
            }

            final AtomicInteger i = new AtomicInteger(0);
            profileHandler.getGrantsByStaff(profile.getUuid())
                    .stream()
                    .filter(grant -> !grant.isRemoved())
                    .filter(Grant::isActive)
                    .filter(grant -> (System.currentTimeMillis() - grant.getAddedAt()) <= duration)
                    .forEach(grant -> {
                        grant.removeGrant(
                                removedBy.get(),
                                System.currentTimeMillis(),
                                reason);

                        i.getAndIncrement();
                    });

            sender.sendMessage(Color.translate("&aSuccessfully rolled back " + i.get() + " grants."));
        });
    }
}
