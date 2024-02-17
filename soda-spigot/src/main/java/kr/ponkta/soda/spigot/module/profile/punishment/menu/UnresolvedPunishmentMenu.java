package kr.ponkta.soda.spigot.module.profile.punishment.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.pagination.PaginatedMenu;
import com.google.common.collect.ImmutableList;
import kr.ponkta.soda.spigot.Soda;
import kr.ponkta.soda.spigot.PrimeConstants;
import kr.ponkta.soda.spigot.module.profile.Profile;
import kr.ponkta.soda.spigot.module.profile.ProfileHandler;
import kr.ponkta.soda.spigot.module.profile.punishment.Punishment;
import kr.ponkta.soda.spigot.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class UnresolvedPunishmentMenu extends PaginatedMenu {

    private final Soda plugin = JavaPlugin.getPlugin(Soda.class);
    private final ProfileHandler profileHandler = plugin.getModuleHandler().getModule(ProfileHandler.class);
    private final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm");

    private final Profile profile;

    public UnresolvedPunishmentMenu(Profile profile) {
        this.profile = profile;
        format.setTimeZone(TimeZone.getTimeZone("America/New_York"));
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Unresolved Punishments";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        final AtomicInteger slot = new AtomicInteger(0);
        this.profileHandler.punishmentsWithoutProof(profile)
                .stream()
                .sorted(Comparator.comparingLong(Punishment::getAddedAt).reversed())
                .forEach(punishment -> {
                    buttons.put(slot.getAndIncrement(), new Button() {
                        @Override
                        public String getName(Player player) {
                            return ChatColor.GOLD + format.format(punishment.getAddedAt());
                        }

                        @Override
                        public List<String> getDescription(Player player) {
                            final List<String> lore = Arrays.asList(
                                    "&eBy: &c" + (punishment.getAddedBy().equals(PrimeConstants.CONSOLE_UUID) ?
                                            "&4&lConsole" : profileHandler.getProfile(punishment.getAddedBy()).isPresent() ?
                                            profileHandler.getProfile(punishment.getAddedBy()).get().getColoredName() : "Unknown"),
                                    "&eReason: &c" + punishment.getAddedReason(),
                                    "&eRemaining: &c" + punishment.formatDuration(),
                                    Color.SPACER_SHORT
                            );

                            if(punishment.isRemoved()) {
                                lore.add("&c&lRemoved");
                                lore.add(" ");
                                lore.add("&eBy: &c" + (punishment.getRemovedBy().equals(PrimeConstants.CONSOLE_UUID) ?
                                        "&4&lConsole" : profileHandler.getProfile(punishment.getRemovedBy()).isPresent() ?
                                        profileHandler.getProfile(punishment.getRemovedBy()).get().getColoredName() : "Unknown"));
                                lore.add("&eReason: &c" + punishment.getRemovedReason());
                                lore.add(" ");
                                lore.add(ChatColor.GOLD + format.format(punishment.getRemovedAt()));
                                lore.add(Color.SPACER_SHORT);
                            }

                            lore.add("&eClick to view evidence.");
                            lore.add(Color.SPACER_SHORT);

                            return Color.translate(lore);
                        }

                        @Override
                        public Material getMaterial(Player player) {
                            return punishment.getItemStack().getType();
                        }

                        @Override
                        public byte getDamageValue(Player player) {
                            return (byte) punishment.getItemStack().getDurability();
                        }

                        @Override
                        public void clicked(Player player, int slot, ClickType clickType) {
                            new PunishmentEvidenceMenu(profile, punishment).openMenu(player);
                        }
                    });
                });

        return buttons;
    }
}
