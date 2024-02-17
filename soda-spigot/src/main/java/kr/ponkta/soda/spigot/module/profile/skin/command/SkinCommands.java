package kr.ponkta.soda.spigot.module.profile.skin.command;

import com.elevatemc.elib.command.Command;
import kr.ponkta.soda.spigot.module.profile.skin.menu.SkinSelectMenu;
import org.bukkit.entity.Player;

public class SkinCommands {

    @Command(names = {"skinchanger"}, permission = "Soda.command.skinchanger", description = "Open up the Skin Changer")
    public static void open(Player player) {
        new SkinSelectMenu().openMenu(player);
    }
}
