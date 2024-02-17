package kr.ponkta.soda.spigot.module.profile.punishment.type;

import kr.ponkta.soda.spigot.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public enum PunishmentType {
    WARN("warned", "Warns", new ItemBuilder(Material.WOOL).dur(5).build(), 17, "Soda.history.warn"),
    MUTE("muted", "Mutes", new ItemBuilder(Material.WOOL).dur(4).build(), 15, "Soda.history.mute"),
    GHOSTMUTE("ghost muted", "Ghost Mutes", new ItemBuilder(Material.WOOL).dur(1).build(), 13, "Soda.history.ghostmute"),
    BAN("banned", "Bans", new ItemBuilder(Material.WOOL).dur(14).build(), 11, "Soda.history.ban"),
    BLACKLIST("blacklisted", "Blacklists", new ItemStack(Material.BEDROCK), 9, "Soda.history.blacklist");

    String display, menu;
    ItemStack menuStack;
    int slot;
    String permission;

    PunishmentType(String display, String menu, ItemStack menuStack, int slot, String permission) {
        this.display = display;
        this.menu = menu;
        this.menuStack = menuStack;
        this.slot = slot;
        this.permission = permission;
    }
}
