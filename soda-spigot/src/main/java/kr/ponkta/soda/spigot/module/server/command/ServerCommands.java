package kr.ponkta.soda.spigot.module.server.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.elib.util.TimeUtils;
import kr.ponkta.soda.spigot.Soda;
import kr.ponkta.soda.spigot.module.ModuleHandler;
import kr.ponkta.soda.spigot.module.database.redis.JedisModule;
import kr.ponkta.soda.spigot.module.server.ServerHandler;
import kr.ponkta.soda.spigot.module.server.menu.ServerGroupMenu;
import kr.ponkta.soda.spigot.util.Color;
import kr.ponkta.soda.spigot.util.time.DurationUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ServerCommands {

    private static final Soda plugin = Soda.getInstance();
    private static final ModuleHandler moduleHandler = plugin.getModuleHandler();
    
    private static final ServerHandler serverHandler = moduleHandler.getModule(ServerHandler.class);
    private static final JedisModule jedisModule = moduleHandler.getModule(JedisModule.class);

    @Command(names = {"prime servers"}, permission = "Soda.command.servers")
    public static void executeServers(Player player) {
        new ServerGroupMenu().openMenu(player);
    }

    @Command(names = {"server list"}, permission = "Soda.command.server.list")
    public static void executeList(CommandSender sender) {
        serverHandler.getServers().forEach(server -> sender.sendMessage(Color.translate("&7- &e" + server.getName() + " &7(WL: &r" + (server.isWhitelisted() ? "&aYes" : "&cNo") + "&7) (Players: &f" + server.getPlayers() + "/" + server.getMaxPlayers() + "&7) (Alive: &r" + (server.isAlive() ? "&aYes" : "&cNo") + "&7)")));
    }

    @Command(names = {"servergroup list"}, permission = "Soda.command.server.list")
    public static void executeGroupList(CommandSender sender) {
        serverHandler.getServerGroups().forEach(group -> {
            sender.sendMessage(Color.translate("&7- &e" + group.getId() + " &7(Servers: &f" + serverHandler.getServersWithGroup(group).size() + "&7)"));
        });
    }

    @Command(names = {"motd get"}, permission = "Soda.command.motd.get")
    public static void executeMotdGet(CommandSender sender) {
        jedisModule.runCommand(jedis -> Arrays.asList(
                jedis.hget("Prime:MOTD", "1"),
                jedis.hget("Prime:MOTD", "2"),
                Color.translate("&7Countdown: &f" + TimeUtils.formatIntoDetailedString(
                        (int)((Long.parseLong(jedis.hget("Prime:MOTD", "countdown")) - System.currentTimeMillis())/1000)
                ))
        ).forEach(sender::sendMessage));
    }

    @Command(names = {"motd update"}, permission = "Soda.command.motd.update")
    public static void executeMotdUpdate(CommandSender sender, @Parameter(name = "line") int line, @Parameter(name = "motd", wildcard = true) String text) {
        if(line < 1 || line > 2) {
            sender.sendMessage(Color.translate("&cThat is not a valid motd line."));
            return;
        }

        jedisModule.runCommand(jedis -> jedis.hset("Prime:MOTD", String.valueOf(line), Color.translate(text + "&r")));
        sender.sendMessage(Color.translate("&aSuccessfully updated line " + line + " of the motd."));
    }

    @Command(names = {"motd setcountdown"}, permission = "Soda.command.motd.update")
    public static void countdown(CommandSender sender, @Parameter(name = "time in millis") long millis) {
        jedisModule.runCommand(jedis -> jedis.hset("Prime:MOTD", "countdown", String.valueOf(millis)));
        sender.sendMessage(Color.translate("&aSuccessfully updated the countdown date. &aUse the %countdown% &eplaceholder to access."));
    }

    @Command(names = {"mutechat"}, permission = "Soda.command.mutechat")
    public static void executeMuteChat(CommandSender sender) {
        serverHandler.getCurrentServer().setChatMuted(!serverHandler.getCurrentServer().isChatMuted());
        Bukkit.broadcastMessage(Color.translate("&dThe chat has been " + (serverHandler.getCurrentServer().isChatMuted() ? "muted" : "unmuted") + " &dby a staff member."));
    }

    @Command(names = {"slowchat"}, permission = "Soda.command.slowchat")
    public static void slowchat(CommandSender sender, @Parameter(name = "seconds", defaultValue = "0") int seconds) {
        Bukkit.broadcastMessage(Color.translate(seconds == 0 ? "&dThe chat is no longer slowed." : "&dThe chat has been slowed by a staff member."));
        serverHandler.getCurrentServer().setChatSlow(seconds * 1000L);
        Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("Soda.staff")).forEach(staff -> {
            staff.sendMessage(Color.translate("&b[S] &3The chat has been " + (seconds == 0 ? "&aunslowed&3." : "&cslowed &3to &c" + seconds + " &3seconds.")));
        });
    }
}
