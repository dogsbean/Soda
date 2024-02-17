package kr.ponkta.soda.spigot.module.tag.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import kr.ponkta.soda.spigot.Soda;
import kr.ponkta.soda.spigot.module.ModuleHandler;
import kr.ponkta.soda.spigot.module.profile.ProfileHandler;
import kr.ponkta.soda.spigot.module.tag.Tag;
import kr.ponkta.soda.spigot.module.tag.TagHandler;
import kr.ponkta.soda.spigot.module.tag.menu.TagMenu;
import kr.ponkta.soda.spigot.util.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TagCommands {

    private static final Soda plugin = Soda.getInstance();
    private static final ModuleHandler moduleHandler = plugin.getModuleHandler();

    private static final TagHandler tagHandler = moduleHandler.getModule(TagHandler.class);
    private static final ProfileHandler profileHandler = moduleHandler.getModule(ProfileHandler.class);

    @Command(names = {"tag", "tags", "prefix"}, permission = "Soda.command.tag")
    public static void select(Player player) {
        new TagMenu().openMenu(player);
    }

    @Command(names = {"tag list", "tags list"}, permission = "op")
    public static void list(CommandSender sender) {
        sender.sendMessage(Color.translate("&6Prime Tags -"));
        tagHandler.getTags().values().forEach(tag -> {
            sender.sendMessage(String.format(Color.translate("ID: %s&r, Display Name: %s&r, Display: %s&r"), tag.getId(), Color.translate(tag.getDisplayName()), Color.translate(tag.getDisplay())));
        });
    }

    @Command(names = {"tag create", "tags create"}, permission = "op")
    public static void create(CommandSender sender, @Parameter(name = "id") String id, @Parameter(name = "display") String display, @Parameter(name = "displayName", wildcard = true) String displayName) {
        if(tagHandler.getTag(id) != null) {
            sender.sendMessage(Color.translate("&cA tag with that id already exists"));
            return;
        }

        final Tag tag = new Tag(id, displayName, display);
        tagHandler.create(tag);

        sender.sendMessage(Color.translate("&aCreated " + tag.getDisplayName()));
    }

    @Command(names = {"tag delete", "tags delete", "tag remove", "tags remove"}, permission = "op")
    public static void delete(CommandSender sender, @Parameter(name = "tag") Tag tag) {
        tagHandler.delete(tag);
        sender.sendMessage(Color.translate("&aDeleted " + tag.getDisplayName()));
    }
}
